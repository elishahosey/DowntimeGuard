package com.example.downtimeguard.utils

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import androidx.core.app.ActivityCompat

class PermissionsUtil {
    var context: Context? = null


    companion object {
        fun requestAllPermissions(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity, arrayOf<String>(
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.PACKAGE_USAGE_STATS
                ), 1
            )
        }

        //check for permissions, otherwise notify user
        fun verifyPermissions(context: Context) {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager?
            //        int mode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
//                android.os.Process.myUid(), getPackageName());
        }
    }
}
