package com.example.downtimeguard
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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

@Composable
fun MainScreenUI(navController: NavController) {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}

