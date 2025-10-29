import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.downtimeguard.data.model.BlockRule
import com.example.downtimeguard.ui.theme.viewmodel.AppUsageViewModel
import kotlinx.coroutines.delay

@Composable
fun EmergencyUnlockButton(
    viewModel: AppUsageViewModel = hiltViewModel<AppUsageViewModel>(),
    requiredUprightMs: Long = 8_000L,   // must stand ~8s
    holdMs: Long = 3_000L               // then hold 3s
) {
    val rule by viewModel.ruleFlow.collectAsState(initial = BlockRule())
    val monthKey = remember { java.time.YearMonth.now().toString() }
    val used = if (rule.emergencyMonth == monthKey) rule.emergencyUsed else 0
    val remaining = (5 - used).coerceAtLeast(0)

    val context = LocalContext.current
    var uprightForMs by remember { mutableStateOf(0L) }
    var isUpright by remember { mutableStateOf(false) }
    var isPressing by remember { mutableStateOf(false) }
    var pressProgressMs by remember { mutableStateOf(0L) }
    val readyToAttempt = remaining > 0
    // Listen to posture broadcasts
    DisposableEffect(Unit) {
        val r = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, i: Intent?) {
                isUpright = i?.getBooleanExtra("isUpright", false) ?: false
                uprightForMs = i?.getLongExtra("uprightForMs", 0L) ?: 0L
            }
        }
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(r, IntentFilter("DG_POSTURE"))
        onDispose { LocalBroadcastManager.getInstance(context).unregisterReceiver(r) }
    }

    // Hold-to-unlock timer (only counts while upright requirement is satisfied)
    LaunchedEffect(isPressing, isUpright, uprightForMs) {
        if (!isPressing) { pressProgressMs = 0L; return@LaunchedEffect }
        while (isPressing) {
            if (uprightForMs >= requiredUprightMs) {
                delay(50)
                pressProgressMs += 50
                if (pressProgressMs >= holdMs) {
                    // Grant temporary unlock for rule.unlockDurationMin minutes
                    viewModel.requestEmergencyUnlock()
                    isPressing = false
                    pressProgressMs = 0L
                }
            } else {
                // Not upright long enough; don’t count
                pressProgressMs = 0L
                delay(100)
            }
        }
    }

    val ready = uprightForMs >= requiredUprightMs
    val holdPct = (pressProgressMs.toFloat() / holdMs).coerceIn(0f, 1f)

    Button(
        enabled = readyToAttempt, // also include upright readiness if you want
        onClick = { /* long-press path handles completion; keep as in your version */ }
    ) {
        Text(
            if (remaining > 0)
                "Emergency Unlock (left: $remaining)"
            else
                "No unlocks left this month"
        )
    }

    Button(
        onClick = { /* handled by press */ },
        enabled = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(ready) {
                detectTapGestures(
                    onPress = {
                        isPressing = true
                        tryAwaitRelease()
                        isPressing = false
                    }
                )
            }
    ) {
        val label = when {
            !ready -> "Stand up to unlock (${(requiredUprightMs - uprightForMs).coerceAtLeast(0).div(1000)}s)"
            isPressing -> "Hold… ${(holdPct * 100).toInt()}%"
            else -> "Emergency Unlock (press & hold)"
        }
        Text(label)
    }
}
