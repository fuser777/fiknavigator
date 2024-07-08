package com.unisza.fiknavigator.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "node_connection",
    foreignKeys = [
        ForeignKey(entity = Node::class, parentColumns = ["node_id"], childColumns = ["node1"]),
        ForeignKey(entity = Node::class, parentColumns = ["node_id"], childColumns = ["node2"])
    ],
    indices = [
        Index("node1"),
        Index("node2")
    ]
)
data class NodeConnection(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "connection_id")
    val connectionId: Int?,

    @ColumnInfo(name = "node1")
    val node1: Int?,

    @ColumnInfo(name = "node2")
    val node2: Int?,

    @ColumnInfo(name = "weight")
    val weight: Int?
)