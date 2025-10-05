package com.example.downtimeguard.ui.theme

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.downtimeguard.data.model.AppItem
import com.example.downtimeguard.ui.theme.viewmodel.AppUsageViewModel


@Composable
fun AppPickerScreen(
    navController: NavController,
    viewModel: AppUsageViewModel = hiltViewModel<AppUsageViewModel>(),// tell which hilt view to reference or from dependency injection graph
    onDone: (List<AppItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) { viewModel.refresh() }

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
    var selectedIds by rememberSaveable { mutableStateOf(listOf<String>()) }
    fun toggle(id: String) {
        selectedIds = if (id in selectedIds) selectedIds - id else selectedIds + id
    }

    Column(modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = { Text("Search apps") },
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
                    text = "Selected: ${'$'}{selectedIds.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {
                    val picked = allApps.filter { it.id in selectedIds }
                    onDone(picked)
                }) { Text("Done") }
            }
        }
    }
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
            Text(app.title, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(app.id, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
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