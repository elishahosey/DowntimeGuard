package com.example.downtimeguard.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.downtimeguard.data.model.AppItem
import com.example.downtimeguard.data.model.toAppItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton //tell Hilt this is on the application level shared across the app
class AppRepository @Inject constructor(
    @ApplicationContext val context: Context
){
    private val _apps = MutableStateFlow<List<AppItem>>(emptyList())
    val pm: PackageManager = context.packageManager
    val apps = _apps.asStateFlow() //viewModel checks this

    //packaging apps in a certain way on a different thread (takes time)
    suspend fun loadApps(): List<AppItem> = withContext(Dispatchers.IO) {
        //starting the intent
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        //find all activities for this intent ,hence 0
        pm.queryIntentActivities(intent, 0)
            .asSequence()
            .map { it.activityInfo }
            .filter { info -> //sneaking google through for now (big distractor for me)
                val isSystemApp = (info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                !isSystemApp || info.packageName == "com.android.chrome"
            }
            .map { info ->
                info.applicationInfo.toAppItem(pm)
            }
            .distinctBy { it.id }
            .sortedBy { it.title.lowercase() }
            .toList()
    }


    suspend fun refreshApps(){
        _apps.value=loadApps()
    }

}