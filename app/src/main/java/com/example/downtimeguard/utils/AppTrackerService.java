package com.example.downtimeguard.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;

public class AppTrackerService {

    private final Context context;

    public AppTrackerService(Context context) {
        this.context = context;
    }

    public boolean checkUsageStatsPermission() {
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mode = appOpsManager.unsafeCheckOpNoThrow(
                    "android:get_usage_stats",
                    Process.myUid(),
                    context.getPackageName()
            );
        } else {
            mode = appOpsManager.checkOpNoThrow(
                    "android:get_usage_stats",
                    Process.myUid(),
                    context.getPackageName()
            );
        }

        return mode == AppOpsManager.MODE_ALLOWED;
    }
}
