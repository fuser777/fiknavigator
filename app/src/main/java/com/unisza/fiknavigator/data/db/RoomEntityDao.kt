package com.unisza.fiknavigator.data.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface RoomEntityDao {
    @Query("SELECT room_alias FROM room WHERE room.room_id = :nodeId")
    fun getRoomAliasesWithNodeId(nodeId: Int): List<String>
}