package com.example.downtimeguard
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import com.example.downtimeguard.ui.theme.DowntimeGuardTheme
import com.example.downtimeguard.utils.AppTrackerService

class MainActivity : AppCompatActivity() {
    private var stat : Boolean = false
    private lateinit var  ats : AppTrackerService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val navController = rememberNavController()
            DowntimeGuardTheme {
                NavGraph(navController)
            }
            }

        ats=AppTrackerService(this)
        stat= ats.checkUsageStatsPermission()

        if (!stat) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
//        PermissionsUtil.requestAllPermissions(this)

//        val blockerSwitch: Switch = findViewById(R.id.switch_blocker)

//        switch to enable blocking
//        blockerSwitch.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                startService(Intent(this, SensorService::class.java))
//                ActivityRecognitionHelper.requestActivityUpdates(this)
//            } else {
//                stopService(Intent(this, SensorService::class.java))
//                ActivityRecognitionHelper.removeActivityUpdates(this)
//            }
//        }
    }
    override fun onPause() {
        super.onPause()
        // Pause animations, videos, sensors, etc.
    }

    //1-request permission for app tracking
    //2- if yes-track, otherwise show alert or notif of request

    override fun onStop() {
        super.onStop()
        // Release resources or save more persistent state
    }

    override fun onResume() {
        super.onResume()
        // Restart UI updates, resume activity
        if (stat) {
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Usage access not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTrackingUsage() {
        // This is where your logic goes (e.g. read UsageStatsManager, update DB, etc.)
        Log.d("DowntimeGuard", "Starting to track app usage...")
    }

}

