package com.unisza.fiknavigator.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "room",
    foreignKeys = [ForeignKey(
        entity = Node::class,
        parentColumns = ["node_id"],
        childColumns = ["room_id"],
        onDelete = ForeignKey.CASCADE // Ensure onDelete behavior is specified correctly
    )],
    indices = [Index("room_id")]
)
data class RoomEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int?,

    @ColumnInfo(name = "room_id")
    val roomId: Int?,

    @ColumnInfo(name = "room_alias")
    val roomAlias: String?
)