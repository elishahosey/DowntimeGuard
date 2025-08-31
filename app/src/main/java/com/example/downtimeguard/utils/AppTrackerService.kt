package com.example.downtimeguard.utils

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import android.os.Process

class AppTrackerService(private val context: Context) {
    fun checkUsageStatsPermission(): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mode = appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(),
                context.getPackageName()
            )
        } else {
            mode = appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(),
                context.getPackageName()
            )
        }

        return mode == AppOpsManager.MODE_ALLOWED
    }
}
