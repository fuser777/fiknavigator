package com.unisza.fiknavigator.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "building")
data class Building(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "building_id")
    val buildingId: Int?,

    @ColumnInfo(name = "building_code")
    val buildingCode: String?,

    @ColumnInfo(name = "building_name")
    val buildingName: String?
)