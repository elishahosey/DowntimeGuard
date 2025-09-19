package com.example.downtimeguard.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log


class AppRepository (context: Context){
    val TAG = "AppRepository"
    val pm: PackageManager = context.packageManager
    //get a list of installed apps.
    @SuppressLint("QueryPermissionsNeeded")
    val packages: List<ApplicationInfo> = pm.getInstalledApplications(PackageManager.GET_META_DATA)

    init {
        Log.v("AppRepository", "Repository created"+ pm)
    }

}