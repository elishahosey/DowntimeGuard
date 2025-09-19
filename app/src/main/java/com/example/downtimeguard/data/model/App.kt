package com.example.downtimeguard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "app_list")
data class App (
//    @Embedded val appId: AppUsageInfo,
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
//    TODO: Figure out link of id to usage, with AppUsageInfo

//    @Relation(
//        entity = ,
//        parentColumn = "appId",
//        entityColumn = "id"
//
//    )
)