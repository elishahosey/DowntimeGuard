package com.example.downtimeguard.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.downtimeguard.data.DataStoreManager
import com.example.downtimeguard.data.model.AppItem
import com.example.downtimeguard.data.model.AppUsageInfo
import com.example.downtimeguard.data.model.AppUsageSummary
import com.example.downtimeguard.data.model.BlockRule
import com.example.downtimeguard.data.repository.AppRepository
import com.example.downtimeguard.data.repository.AppUsageRepository
import com.example.downtimeguard.services.AppTrackerServices
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class EmergencyUiState(
    val remaining: Int = 5,
    val monthlyLimit: Int = 5
)

sealed interface UiEvent {
    data class ShowSnack(val msg: String): UiEvent
}

@HiltViewModel //needed automatically pass in viewmodel bundle/state
class AppUsageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppUsageRepository,
    private val appRepository: AppRepository,
    private val ds: DataStoreManager
) : ViewModel() {
    val apps: LiveData<List<AppItem>> = appRepository.apps.asLiveData()
    val ruleFlow = ds.ruleFlow



    // public LiveData so UI can observe it
    private val _isPermissionGranted = MutableLiveData(false)
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    // Make sure your service exposes this as LiveData<Boolean>
    val isServiceRunning: LiveData<Boolean> = AppTrackerServices.isRunning

    // The UI expects LiveData<List<AppUsageInfo>>
    val todayEvents: LiveData<List<AppUsageInfo>> =
        repository.getTodayAppUsageEvents().asLiveData()

    val uiState: StateFlow<EmergencyUiState> =
        ds.ruleFlow.map { rule ->
            val month = java.time.YearMonth.now().toString()
            val used = if (rule.emergencyMonth == month) rule.emergencyUsed else 0
            EmergencyUiState(
                remaining = (5 - used).coerceAtLeast(0),
                monthlyLimit = 5
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, EmergencyUiState())


    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events


    fun setPermissionGranted(isGranted: Boolean) {
        _isPermissionGranted.value = isGranted
    }

    // Build per-app summary from today's events
    val appUsageSummary: LiveData<List<AppUsageSummary>> =
        repository.getTodayAppUsageEvents()
            .map { events ->
                events
                    .groupBy { it.packageName }
                    .map { (packageName, appEvents) ->
                        val appName = appEvents.firstOrNull()?.appName ?: packageName
                        val totalDuration = appEvents.sumOf { it.durationMillis }
                        val count = appEvents.size
                        val lastUsed = appEvents.maxOfOrNull { it.startTimeMillis } ?: 0L

                        // Use named args to match your data class
                        AppUsageSummary(
                            packageName = packageName,
                            appName = appName,
                            totalDurationMillis = totalDuration,
                            launchCount = count,
                            lastUsedTimeStamp = lastUsed
                        )
                    }
                    .sortedByDescending { it.totalDurationMillis }
            }
            .asLiveData()

    fun formatDuration(durationMillis: Long): String {
        val seconds = (durationMillis / 1000) % 60
        val minutes = (durationMillis / (1000 * 60)) % 60
        val hours = durationMillis / (1000 * 60 * 60)
        return when {
            hours > 0   -> String.format("%d h %d min", hours, minutes)
            minutes > 0 -> String.format("%d min %d sec", minutes, seconds)
            else        -> String.format("%d sec", seconds)
        }
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun refresh() = viewModelScope.launch { appRepository.refreshApps() }

    // Saves selected apps as a blocking rule
    // and marks the rule as enabled (active).
    fun saveRule(selectedPackages: Set<String>) = viewModelScope.launch {
        ds.saveRule(BlockRule(enabled = true, packages = selectedPackages))
    }


    fun requestEmergencyUnlock() = viewModelScope.launch {
        val rule = ruleFlow.first()
        val month = currentMonthKey()

        val usedThisMonth = if (rule.emergencyMonth == month) rule.emergencyUsed else 0
        val limit = 5

        //pass to UI
        if (usedThisMonth >= limit) {
            _events.emit(UiEvent.ShowSnack("No emergency unlocks left this month"))
            return@launch
        }

        // Grant temporary unlock
        ds.setTemporaryUnlock(rule.unlockDurationMin)
        ds.saveRule(
            rule.copy(
                emergencyUsed = usedThisMonth + 1,
                emergencyMonth = month
            )
        )
    }


    private fun currentMonthKey(): String =
        YearMonth.now(ZoneId.systemDefault()).toString()

    val selectedAppIds = ds.selectedAppsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptySet()
    )

    fun toggleAppSelected(id: String) {
        viewModelScope.launch {
            ds.toggleAppSelection(id)
        }
    }


}
