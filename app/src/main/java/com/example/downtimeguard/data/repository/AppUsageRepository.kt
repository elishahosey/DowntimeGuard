package com.example.downtimeguard.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.example.downtimeguard.data.local.AppDatabase
import com.example.downtimeguard.data.model.AppUsageInfo
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class AppUsageRepository(
    private val context: Context
) {
    private val dao = AppDatabase.get(context).appUsageDao()

    suspend fun logAppUsageEvent(
        packageName: String,
        startTime: Long,
        endTime: Long = System.currentTimeMillis(),
        userId: String? = null
    ) {
        try {
            val pm = context.packageManager
            val appName = try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                pm.getApplicationLabel(appInfo).toString()
            } catch (_: PackageManager.NameNotFoundException) {
                packageName
            }

            val duration = (endTime - startTime).coerceAtLeast(0)

            val event = AppUsageInfo(
                packageName = packageName,
                appName = appName,
                startTimeMillis = startTime,
                endTimeMillis = endTime,
                durationMillis = duration,
                userId = userId
            )
            dao.insert(event)
        } catch (e: Exception) {
            Log.e("AppUsageRepository", "Error logging app usage event", e)
        }
    }

    fun getTodayAppUsageEvents(): Flow<List<AppUsageInfo>> {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return dao.getEventsSince(startOfDay)
    }
}
