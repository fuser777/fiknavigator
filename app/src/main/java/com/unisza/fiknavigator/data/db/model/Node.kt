package com.unisza.fiknavigator.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "node",
    foreignKeys = [
        ForeignKey(
            entity = Building::class,
            parentColumns = ["building_id"],
            childColumns = ["building_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("building_id")]
)
data class Node(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "node_id")
    val nodeId: Int?,

    @ColumnInfo(name = "x")
    val x: Int?,

    @ColumnInfo(name = "y")
    val y: Int?,

    @ColumnInfo(name = "z")
    val z: Int?,

    @ColumnInfo(name = "node_type")
    val nodeType: String?,

    @ColumnInfo(name = "node_name")
    val nodeName: String?,

    @ColumnInfo(name = "building_id")
    val buildingId: Int?
)