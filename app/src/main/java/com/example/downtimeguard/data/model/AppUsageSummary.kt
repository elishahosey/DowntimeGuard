package com.example.downtimeguard.data.model

data class AppUsageSummary (
    val packageName: String,
    val appName:String,
    val totalDurationMillis:Long,
    val launchCount:Int,
    val lastUsedTimeStamp:Long
)