package com.example.downtimeguard
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.downtimeguard.ui.theme.AppTrackerScreen
import com.example.downtimeguard.ui.theme.DowntimeGuardTheme
import com.example.downtimeguard.ui.theme.viewmodel.AppUsageViewModel
import com.example.downtimeguard.utils.AppTrackerService

class MainActivity : AppCompatActivity() {
    private var stat: Boolean = false
    private lateinit var ats: AppTrackerService
    private val viewModel: AppUsageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //val navController = rememberNavController()
            DowntimeGuardTheme {
               // NavGraph(navController)
            }
                AppTrackerScreen(viewModel)
        }


        //TODO: hoist this pass other UIs
        ats = AppTrackerService(this)
        stat = ats.checkUsageStatsPermission()

        if (!stat) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        checkForPermission()
    }


    override fun onResume() {
        super.onResume()
        checkforAccessibilityPermission()
        Log.d("MainActivity", "onResume: Checking accessibility permission")
    }

    //TODO: See why accessibility is resetting to false
    private fun checkforAccessibilityPermission() {
        val accessibilityEnabled = isAccessibilityServiceEnabled()
        Log.d("MainActivity", "Permission check result: $accessibilityEnabled")
        viewModel.setPermissionGranted(accessibilityEnabled)
    }

    private fun checkForPermission() {
        val granted = hasUsageStatsPermission(this)
        viewModel.setPermissionGranted(granted)
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.packageName
            )
        }

        return mode == AppOpsManager.MODE_ALLOWED
    }


    private fun isAccessibilityServiceEnabled(): Boolean {
        val serviceName = packageName + "/" + AppTrackerService::class.java.canonicalName
        val accessibilityEnabled = try {
            Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Settings.SettingNotFoundException) {
            Log.e("MainActivity", "Error finding accessibility settings", e)
            0
        }
        Log.e("MainActivity", "Accessibility enabled: $accessibilityEnabled")

        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            Log.d("MainActivity", "Enabled services: $settingValue")
            Log.d("MainActivity", "Looking for services: $serviceName")

            return settingValue.split(":").contains(serviceName)

        }
        return false
    }
}


