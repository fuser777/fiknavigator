package com.unisza.fiknavigator.data.model

import androidx.room.ColumnInfo

data class ResultNode(
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

    @ColumnInfo(name = "building_code")
    val buildingCode: String?,
)