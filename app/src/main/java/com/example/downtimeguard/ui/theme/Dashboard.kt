package com.example.downtimeguard.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Welcome to the Dashboard!")

                Spacer(modifier = Modifier.height(20.dp))

                // Start Tracking button, permission already granted
                //TODO once button is clicked, pick list of apps
                //  1)first glance 2) in depth review => thru charts
                Button(onClick = {
//                    if (!UsagePermissionUtils.hasUsageAccess(context)) {
//                        // Opens the Usage Access settings if permission isn't granted
//                        context.startActivity(UsagePermissionUtils.usageAccessSettingsIntent())
//                    } else {
//                        // Starts the Java foreground service
//                        val intent = Intent(context, AppTrackerServices::class.java)
//                        ContextCompat.startForegroundService(context, intent)
//                    }
                }) {
                    Text("Go ahead and grab distracting apps!")
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Example navigate button
                Button(onClick = {
                    navController.navigate("settings")
                }) {
                    Text("Go to Settings")
                }
            }
        }
    )
}

//@Composable
//fun EmergencyUnlockButton(
//    vm: AppUsageViewModel = hiltViewModel(),
//    requiredUprightMs: Long = 8_000L,   // must stand ~8s
//    holdMs: Long = 3_000L,               // then hold 3s
//    viewModel: AppUsageViewModel
//) {
//    val context = LocalContext.current
//    var uprightForMs by remember { mutableStateOf(0L) }
//    var isUpright by remember { mutableStateOf(false) }
//    var isPressing by remember { mutableStateOf(false) }
//    var pressProgressMs by remember { mutableStateOf(0L) }
//
//    // Listen to posture broadcasts
//    DisposableEffect(Unit) {
//        val r = object : BroadcastReceiver() {
//            override fun onReceive(c: Context?, i: Intent?) {
//                isUpright = i?.getBooleanExtra("isUpright", false) ?: false
//                uprightForMs = i?.getLongExtra("uprightForMs", 0L) ?: 0L
//            }
//        }
//        LocalBroadcastManager.getInstance(context)
//            .registerReceiver(r, IntentFilter("DG_POSTURE"))
//        onDispose { LocalBroadcastManager.getInstance(context).unregisterReceiver(r) }
//    }
//
//    // Hold-to-unlock timer (only counts while upright requirement is satisfied)
//    LaunchedEffect(isPressing, isUpright, uprightForMs) {
//        if (!isPressing) { pressProgressMs = 0L; return@LaunchedEffect }
//        while (isPressing) {
//            if (uprightForMs >= requiredUprightMs) {
//                delay(50)
//                pressProgressMs += 50
//                if (pressProgressMs >= holdMs) {
//                    // Grant temporary unlock for rule.unlockDurationMin minutes
//                    vm.requestEmergencyUnlock()
//                    isPressing = false
//                    pressProgressMs = 0L
//                }
//            } else {
//                // Not upright long enough; don’t count
//                pressProgressMs = 0L
//                delay(100)
//            }
//        }
//    }
//
//    val ready = uprightForMs >= requiredUprightMs
//    val holdPct = (pressProgressMs.toFloat() / holdMs).coerceIn(0f, 1f)
//
//    Button(
//        onClick = { /* handled by press */ },
//        enabled = true,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .pointerInput(ready) {
//                detectTapGestures(
//                    onPress = {
//                        isPressing = true
//                        tryAwaitRelease()
//                        isPressing = false
//                    }
//                )
//            }
//    ) {
//        val label = when {
//            !ready -> "Stand up to unlock (${(requiredUprightMs - uprightForMs).coerceAtLeast(0).div(1000)}s)"
//            isPressing -> "Hold… ${(holdPct * 100).toInt()}%"
//            else -> "Emergency Unlock (press & hold)"
//        }
//        Text(label)
//    }
//}


//TODO Build App Status Chart Screen here
@Preview(showBackground = true,name = "Dashboard Preview")
@Composable
fun DashboardScreenPreview() {
    DowntimeGuardTheme {
        val navController = rememberNavController()
        DashboardScreen(navController)
    }
}
