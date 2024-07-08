package com.unisza.fiknavigator.data.db

import androidx.room.Dao
import androidx.room.Query
import com.unisza.fiknavigator.data.db.model.NodeConnection

@Dao
interface NodeConnectionDao {
    @Query("SELECT * FROM node_connection")
    fun getAllConnections(): List<NodeConnection>
}