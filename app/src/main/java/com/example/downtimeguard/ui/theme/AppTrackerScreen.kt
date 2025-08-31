package com.example.downtimeguard.ui.theme
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.downtimeguard.data.model.AppUsageInfo
import com.example.downtimeguard.data.model.AppUsageSummary
import com.example.downtimeguard.ui.theme.viewmodel.AppUsageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTrackerScreen(viewModel: AppUsageViewModel) {
    val context = LocalContext.current
    val isPermissionGranted by viewModel.isPermissionGranted.observeAsState(initial=false)
    val appUsageSummary by viewModel.appUsageSummary.observeAsState(initial = emptyList())
    val todayEvents: List<AppUsageInfo> by viewModel.todayEvents
        .observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("App Usage Tracker") })
        }
    ) { paddingValues ->
        if (!isPermissionGranted) {
            AccessibilityPermissionRequest(
                onRequestPermission = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    Toast.makeText(
                        context,
                        "Enable AppTrackerService, then return to the app",
                        Toast.LENGTH_LONG
                    ).show()
                },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            AppUsageTabs(
                viewModel = viewModel,
                appUsageSummary = appUsageSummary,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun AppUsageTabs(
    viewModel: AppUsageViewModel,
    appUsageSummary: List<AppUsageSummary>,
    modifier: Modifier = Modifier
) {
    val todayEvents by viewModel.todayEvents.observeAsState(initial = emptyList())
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Summary") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Events") }
            )
        }

        when (selectedTabIndex) {
            0 -> {
                // Summary
                if (appUsageSummary.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No usage summary available yet.")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                "App Usage Summary",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        items(appUsageSummary) { summary ->
                            AppUsageSummaryItem(summary, viewModel)
                            Divider()
                        }
                    }
                }
            }

            1 -> {
                // Detailed Events
                if (todayEvents.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No detailed events available yet.")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                "App Usage Events (${todayEvents.size} events)",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        items(todayEvents) { event ->
                            AppEventItem(event, viewModel)
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppEventItem(event: AppUsageInfo, viewModel: AppUsageViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = event.appName,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Started: ${viewModel.formatDateTime(event.startTimeMillis)}",
                style = MaterialTheme.typography.labelMedium
            )

            if (event.durationMillis > 0) {
                Text(
                    // if you have formatDuration, prefer that over formatDateTime for durations
                    text = "Duration: ${viewModel.formatDuration(event.durationMillis)}",
                    style = MaterialTheme.typography.labelMedium
                )
            } else {
                Text(
                    text = "Brief usage",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun AppUsageSummaryItem(summary: AppUsageSummary, viewModel: AppUsageViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = summary.appName,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Used ${viewModel.formatDuration(summary.totalDurationMillis)}",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "Opened ${summary.launchCount} times",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Last Used:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = viewModel.formatDateTime(summary.lastUsedTimeStamp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun AccessibilityPermissionRequest(onRequestPermission:() -> Unit,modifier:Modifier=Modifier){
    Column(
        modifier=modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
           "This app requires Accessibility Service permission to track app usage",
            modifier = Modifier.padding(horizontal = 32.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Please enable the AppTrackerService in Accessibility Settings",
            modifier = Modifier.padding(horizontal = 32.dp),
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button (onClick = onRequestPermission){
            Text("Open Accessibility Settings")
        }
    }

}
