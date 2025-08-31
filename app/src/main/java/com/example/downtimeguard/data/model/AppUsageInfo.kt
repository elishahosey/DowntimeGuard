// com/example/downtimeguard/data/model/AppUsageInfo.kt
package com.example.downtimeguard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage_events")
data class AppUsageInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val packageName: String,
    val appName: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationMillis: Long,
    val userId: String? = null // keep if you want multi-user later
)
