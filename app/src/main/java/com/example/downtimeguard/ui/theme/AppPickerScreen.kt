package com.example.downtimeguard.ui.theme

import EmergencyUnlockButton
import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.downtimeguard.data.model.AppItem
import com.example.downtimeguard.services.BlockAccessibilityService
import com.example.downtimeguard.services.PostureBlockService
import com.example.downtimeguard.ui.theme.viewmodel.AppUsageViewModel
import com.example.downtimeguard.ui.theme.viewmodel.UiEvent
import kotlinx.coroutines.launch


@Composable
fun AppPickerScreen(
    navController: NavController,
    viewModel: AppUsageViewModel = hiltViewModel<AppUsageViewModel>(),// tell which hilt view to reference or from dependency injection graph
    onDone: (List<AppItem>) -> Unit,
    modifier: Modifier = Modifier
) {
//    LaunchedEffect(Unit) { viewModel.refresh() }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current


    // Listen for one-off events
    LaunchedEffect(Unit) {
        viewModel.refresh()
        viewModel.events.collect { ev ->
            when (ev) {
                is UiEvent.ShowSnack -> snackbarHostState.showSnackbar(ev.msg)
            }
        }
    }

    //collect the apps as they stream through
    val allApps: List<AppItem> by viewModel.apps
        .observeAsState(initial = emptyList())


    //remember nothing at first, filter as searched bar filled out
    //Keep query and filtered list across UI compose changes, updating when query or app list changes
    var query by remember { mutableStateOf("") }
    val filtered = remember(allApps, query) {
        if (query.isBlank()) allApps
        else allApps.filter { it.id.contains(query, true) || it.title.contains(query, true) }
    }

    // Selection that survives config change
    val selectedIds by viewModel.selectedAppIds.collectAsState()
    fun toggle(id: String) {
        viewModel.toggleAppSelected(id)
    }

    Column(modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = { Text("Search apps that you want to block") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No apps found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(items = filtered, key = { it.id }) { app ->
                    AppRow(
                        app = app,
                        selected = app.id in selectedIds,
                        onToggle = { toggle(app.id) }
                    )
                }
            }
        }

        Surface(tonalElevation = 2.dp) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select your apps",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                val scope = rememberCoroutineScope()
//                val context = LocalContext.current
                Button(onClick = {
                    if (!isAccessibilityServiceEnabled(context, BlockAccessibilityService::class.java)) {
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        return@Button // Stop further execution so they can enable it
                    }

                    val picked = allApps.filter { it.id in selectedIds }
                    val selectedPackages = picked.map { it.id }.toSet()
                    scope.launch {
                        viewModel.saveRule(selectedPackages) // store only package IDs
                    }

                    // Start posture sensing (if not already running)
                    ContextCompat.startForegroundService(context,Intent(context, PostureBlockService::class.java))

                    val serviceName = "${context.packageName}/${BlockAccessibilityService::class.java.name}"
                    if (isAccessibilityEnabled(context, serviceName)) {
                        ContextCompat.startForegroundService(context, Intent(context, BlockAccessibilityService::class.java))
                    } else {
                        Toast.makeText(context, "Please enable DowntimeGuard in Accessibility Settings", Toast.LENGTH_LONG).show()
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }


                    (context as? Activity)?.finish()
                }) {
                    Text("Done")
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        EmergencyUnlockButton(
            viewModel = viewModel
        )
        Text(
            "Emergency unlocks left: ${uiState.remaining}/${uiState.monthlyLimit}",
            style = MaterialTheme.typography.bodySmall
        )
    }

}

fun isAccessibilityEnabled(context: Context, serviceName: String): Boolean {
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    return enabledServices.contains(serviceName)
}

fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<out AccessibilityService>): Boolean {
    val expected = "${context.packageName}/${serviceClass.canonicalName}"
    val enabledServices = android.provider.Settings.Secure.getString(
        context.contentResolver,
        android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    return enabledServices.split(":").any { it == expected }
}


@Composable
private fun AppRow(
    app: AppItem,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(app.title, style = MaterialTheme.typography.bodyLarge, maxLines = 1,overflow = TextOverflow.Ellipsis,color=Color.LightGray)
            Text(app.id, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis,color=Color.LightGray)
        }

        Checkbox(checked = selected, onCheckedChange = { onToggle() })
    }
}

// ---- PREVIEW ----
@Preview(showBackground = true)
@Composable
private fun AppPickerScreenPreview() {
    // Drive UI with fake data only. Literally mimicking the stream from viewmodel
    val fakeApps = listOf(
        AppItem(id = "com.android.calendar", title = "Calendar"),
        AppItem(id = "com.android.chrome", title = "Chrome"),
        AppItem(id = "com.google.android.youtube", title = "YouTube"),
        AppItem(id = "com.example.downtimeguard", title = "DowntimeGuard")
    )

    @Composable
    fun PreviewContent(apps: List<AppItem>) {
        var query by remember { mutableStateOf("") }
        val filtered = remember(apps, query) {
            if (query.isBlank()) apps else apps.filter { it.title.contains(query, true) || it.id.contains(query, true) }
        }
        var selected by rememberSaveable { mutableStateOf(listOf<String>()) }
        fun toggle(id: String) { selected = if (id in selected) selected - id else selected + id }

        Column(Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Search apps") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
            LazyColumn(Modifier.weight(1f)) {
                items(filtered, key = { it.id }) { app ->
                    AppRow(app = app, selected = app.id in selected, onToggle = { toggle(app.id) })
                }
            }
            Surface(tonalElevation = 2.dp) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Selected: ${'$'}{selected.size}")
                    Button(onClick = { /* preview noop */ }) { Text("Done") }
                }
            }

        }
    }

    MaterialTheme { PreviewContent(fakeApps) }
}