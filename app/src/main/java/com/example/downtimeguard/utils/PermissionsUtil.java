package com.example.downtimeguard.utils;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.ActivityCompat;

public class PermissionsUtil {
    Context context;


    public static void requestAllPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.PACKAGE_USAGE_STATS
        }, 1);


    }

    //check for permissions, otherwise notify user
    public static void verifyPermissions(Context context){

        AppOpsManager appOps = (AppOpsManager) context.getSystemService(context.APP_OPS_SERVICE);
//        int mode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
//                android.os.Process.myUid(), getPackageName());
    }


}
