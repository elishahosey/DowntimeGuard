package com.example.downtimeguard.ui.theme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.downtimeguard.data.model.AppItem
import com.example.downtimeguard.data.model.AppUsageInfo
import com.example.downtimeguard.data.model.AppUsageSummary
import com.example.downtimeguard.data.repository.AppRepository
import com.example.downtimeguard.data.repository.AppUsageRepository
import com.example.downtimeguard.services.AppTrackerServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel //needed automatically pass in viewmodel bundle/state
class AppUsageViewModel @Inject constructor(
    private val repository: AppUsageRepository,
    private val appRepository: AppRepository
) : ViewModel() {
    val apps: LiveData<List<AppItem>> = appRepository.apps.asLiveData()

    // public LiveData so UI can observe it
    private val _isPermissionGranted = MutableLiveData(false)
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    // Make sure your service exposes this as LiveData<Boolean>
    val isServiceRunning: LiveData<Boolean> = AppTrackerServices.isRunning

    // The UI expects LiveData<List<AppUsageInfo>>
    val todayEvents: LiveData<List<AppUsageInfo>> =
        repository.getTodayAppUsageEvents().asLiveData()



    fun setPermissionGranted(isGranted: Boolean) {
        _isPermissionGranted.value = isGranted
    }

    // Build per-app summary from today's events
    val appUsageSummary: LiveData<List<AppUsageSummary>> =
        repository.getTodayAppUsageEvents()
            .map { events ->                       // name the param!
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
}
