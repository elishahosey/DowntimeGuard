// com/example/downtimeguard/data/local/AppUsageDao.kt
package com.example.downtimeguard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.downtimeguard.data.model.AppUsageInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: AppUsageInfo)

    @Query("""
        SELECT * FROM app_usage_events 
        WHERE startTimeMillis >= :since 
        ORDER BY startTimeMillis DESC
    """)
    fun getEventsSince(since: Long): Flow<List<AppUsageInfo>>
}
