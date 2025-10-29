package com.example.downtimeguard.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.downtimeguard.data.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostureBlockService : Service(), SensorEventListener {

    companion object {
        private const val CHANNEL_ID = "dg_posture_channel"
        private const val NOTIF_ID = 42
    }
    private lateinit var ds: DataStoreManager
    private var blockedPackages = emptySet<String>()

    private lateinit var sensorMgr: SensorManager
    private var uprightSince: Long? = null

    override fun onCreate() {
        super.onCreate()
        ds = DataStoreManager(this)
        // read once at startup
        CoroutineScope(Dispatchers.Main).launch  {
            ds.ruleFlow.collect { rule ->
                blockedPackages = if (rule.enabled) rule.packages else emptySet()
            }
        }
        // 1) Create the notification channel (O+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "DowntimeGuard posture",
                    NotificationManager.IMPORTANCE_LOW
                ).apply { description = "Foreground service while monitoring posture" }
            )
        }

        // 2) Build the notification using *this* as Context
        val notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_more) // replace with your icon
            .setContentTitle("DowntimeGuard active")
            .setContentText("Monitoring posture to block selected apps")
            .setOngoing(true)
            .build()

        // 3) Promote to foreground
        if (Build.VERSION.SDK_INT >= 34) {
            ServiceCompat.startForeground(
                this, NOTIF_ID, notif,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIF_ID, notif)
        }

        // 4) Register sensors
        sensorMgr = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorMgr.registerListener(
            this,
            sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onSensorChanged(e: SensorEvent) {
        val x = e.values[0]; val y = e.values[1]; val z = e.values[2]
        val g = kotlin.math.sqrt(x * x + y * y + z * z)
        val zNorm = z / g
        val yNorm = y / g

        val isUpright = (kotlin.math.abs(zNorm) < 0.2 && kotlin.math.abs(yNorm) > 0.9)

        val now = System.currentTimeMillis()
        uprightSince = when {
            isUpright && uprightSince == null -> now
            isUpright -> uprightSince
            else -> null
        }
        val uprightForMs = uprightSince?.let { now - it } ?: 0L

//        Log.d("PostureBlockService", "Sending posture broadcast: isUpright=$isUpright, layingDown=${!isUpright}")
        LocalBroadcastManager.getInstance(this).sendBroadcast(
            Intent("DG_POSTURE")
                .putExtra("isUpright", isUpright)
                .putExtra("uprightForMs", uprightForMs)
                .putExtra("layingDown", !isUpright)
        )
    }

//    fun blockApp(pkg: String) {
//        val intent = Intent(this, BlockActivity::class.java).apply {
//            putExtra("pkg", pkg)
//            addFlags(
//                Intent.FLAG_ACTIVITY_NEW_TASK or
//                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
//                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
//            )
//        }
//        startActivity(intent)
//    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() {
        sensorMgr.unregisterListener(this)
        super.onDestroy()
    }
}
