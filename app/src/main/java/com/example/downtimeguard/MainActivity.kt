package com.example.downtimeguard
import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.downtimeguard.service.ActivityRecognitionHelper
import com.example.downtimeguard.service.SensorService
import com.example.downtimeguard.ui.theme.DowntimeGuardTheme
import com.example.downtimeguard.ui.theme.MainScreenUI
import com.example.downtimeguard.utils.PermissionsUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            DowntimeGuardTheme {
                MainScreenUI()
            }
            }

        PermissionsUtil.requestAllPermissions(this)

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

    override fun onStop() {
        super.onStop()
        // Release resources or save more persistent state
    }

    override fun onResume() {
        super.onResume()
        // Restart UI updates, resume activity
    }

}

