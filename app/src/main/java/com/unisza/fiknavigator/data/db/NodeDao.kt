package com.unisza.fiknavigator.data.db

import androidx.room.Dao
import androidx.room.Query
import com.unisza.fiknavigator.data.db.model.Node
import com.unisza.fiknavigator.data.model.PathNode
import com.unisza.fiknavigator.data.model.ResultAlias
import com.unisza.fiknavigator.data.model.ResultNode

@Dao
interface NodeDao {
    @Query("""
        SELECT node.node_id, node.x, node.y, node.z, node.node_type, building.building_code
        FROM node
        JOIN building ON node.building_id = building.building_id
    """)
    suspend fun getPathNodes(): List<PathNode>

    @Query("SELECT * FROM node WHERE node_name IS NOT NULL AND z = :floorLevel AND building_id = :department")
    fun getNodesWithFloorLevelAndDepartmentId(floorLevel: Int, department: Int): List<Node>

    @Query("""
        SELECT node.node_id, node.x, node.y, node.z, node.node_type, node.node_name, building.building_code
        FROM node
        JOIN building ON node.building_id = building.building_id
        WHERE node.node_name IS NOT NULL
    """)
    suspend fun getResultNodesWithName(): List<ResultNode>

    @Query("""
        SELECT node.node_id, node.x, node.y, node.z, node.node_type, node.node_name, room.room_alias, building.building_code
        FROM node 
        LEFT JOIN room ON node.node_id = room.room_id
        LEFT JOIN building ON node.building_id = building.building_id
        WHERE node.node_name IS NOT NULL
    """)
    suspend fun getResultNodesWithRoomAlias(): List<ResultAlias>

    @Query("""
        SELECT node.node_id, node.x, node.y, node.z, node.node_type, node.node_name, building.building_code
        FROM node
        JOIN building ON node.building_id = building.building_id
        WHERE node_id = :nodeId
    """)
    suspend fun getNodeWithNodeId(nodeId: Int): List<ResultNode>

    @Query("""
        SELECT node_name
        FROM node
        WHERE node_id = :nodeId
    """)
    fun getNodeNameWithNodeId(nodeId: Int): String

    @Query("""
        SELECT node.node_id, node.x, node.y, node.z, node.node_type, building.building_code
        FROM node
        JOIN building ON node.building_id = building.building_id
        WHERE node.node_id = :nodeId
    """)
    fun getNodeInfo(nodeId: Int): List<PathNode>
}
