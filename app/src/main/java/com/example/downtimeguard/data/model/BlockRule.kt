package com.example.downtimeguard.data.model

data class BlockRule (
    val enabled: Boolean = false,
    val packages: Set<String> = emptySet(),
    val unlockUntil: Long? = null,
    val unlockDurationMin: Int = 15,
    val emergencyUsed: Int = 0,
    val emergencyMonth: String? = null
)

