package com.example.downtimeguard.data.model

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager




//snapshot of apps and details for later reference
data class AppListUIState (
    val appList: List<ApplicationInfo> = emptyList()
)

//specific things for ui
data class AppItem(
    val id: String, //packageName
    val title: String,

)

fun ApplicationInfo.toAppItem(pm: PackageManager): AppItem =
    AppItem(
        id = packageName ?: "",
        title = pm.getApplicationLabel(this).toString()
    )
