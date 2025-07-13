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
import com.example.downtimeguard.utils.AppTrackerService

class MainActivity : AppCompatActivity() {
    private var stat : Boolean = false
    private lateinit var  ats : AppTrackerService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            DowntimeGuardTheme {
                MainScreenUI()
            }
            }

        ats=AppTrackerService(this)
        stat= ats.checkUsageStatsPermission()
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

    //1-request permission for app tracking
    //2- if yes-track, otherwise show alert or notif of request


}

