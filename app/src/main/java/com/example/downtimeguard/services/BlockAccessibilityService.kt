package com.example.downtimeguard.services


import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.downtimeguard.data.DataStoreManager
import com.example.downtimeguard.ui.theme.BlockActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class BlockAccessibilityService : AccessibilityService() {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var ds: DataStoreManager

    @Volatile private var blockedPkgs: Set<String> = emptySet()
    @Volatile private var enabled = false
    @Volatile private var unlockUntil: Long? = null
    @Volatile private var layingDown = false



    override fun onServiceConnected() {
        ds = DataStoreManager(applicationContext) // service has it's own context, so I gotta be specific here
        Log.d("BlockAccessibilityService", "Accessibility connected")
        // Observe rule changes
        CoroutineScope(Dispatchers.Main).launch {
            ds.ruleFlow.collect { rule ->
                blockedPkgs = rule.packages
                enabled = rule.enabled
                unlockUntil = rule.unlockUntil
            }
        }

        // Listen to posture updates from PostureBlockService
        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(c: Context?, i: Intent?) {
                    layingDown = i?.getBooleanExtra("layingDown", false) == true
                }
            },
            IntentFilter("DG_POSTURE")
        )
        val testIntent = Intent("DG_POSTURE").putExtra("layingDown", true)
        LocalBroadcastManager.getInstance(this).sendBroadcast(testIntent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d("BlockAccessibilityService", "Event received: ${event.eventType}, pkg=${event.packageName}")
       if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return

        val now = System.currentTimeMillis()
        val emergencyActive = unlockUntil?.let { now < it } ?: false
        Log.d("BlockAccessibilityService", "enabled: ${enabled}, laying=${layingDown}")
        if (enabled && !emergencyActive && layingDown && pkg in blockedPkgs) {


            val i = Intent(this, BlockActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("pkg", pkg)
            }
            startActivity(i)
        }
    }

    override fun onInterrupt() {}
    override fun onDestroy() { scope.cancel() }
}
