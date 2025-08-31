package com.example.downtimeguard.util

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationUtils {
    const val CHANNEL_ID: String = "downtimeguard_tracking"

    fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                com.example.downtimeguard.util.NotificationUtils.CHANNEL_ID,
                "Downtime Guard Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val nm = ctx.getSystemService<NotificationManager>(NotificationManager::class.java)
            nm.createNotificationChannel(ch)
        }
    }

    fun buildForeground(ctx: Context): Notification {
        com.example.downtimeguard.util.NotificationUtils.ensureChannel(ctx)
        return NotificationCompat.Builder(
            ctx,
            com.example.downtimeguard.util.NotificationUtils.CHANNEL_ID
        )
            .setContentTitle("Downtime Guard")
            .setContentText("Tracking app usage…")
            .setSmallIcon(R.drawable.ic_menu_info_details)
            .setOngoing(true)
            .build()
    }
}
