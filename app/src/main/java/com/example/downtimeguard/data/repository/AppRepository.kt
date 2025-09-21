package com.example.downtimeguard.data.repository

import android.content.Context
import android.content.pm.PackageManager
import com.example.downtimeguard.data.model.AppItem
import com.example.downtimeguard.data.model.toAppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext


class AppRepository (context: Context){
    private val _apps = MutableStateFlow<List<AppItem>>(emptyList())
    val pm: PackageManager = context.packageManager
    val apps = _apps.asStateFlow() //viewModel checks this

    //packaging apps in a certain way on a different thread (takes time)
    suspend fun loadApps(): List<AppItem> = withContext(Dispatchers.IO) {
        val appList =
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .map { it.toAppItem(pm) }            // ApplicationInfo -> AppItem
                .distinctBy { it.id }
                .sortedBy { it.title.lowercase() }

         return@withContext appList
    }

    suspend fun refreshApps(){
        _apps.value=loadApps()
    }

}