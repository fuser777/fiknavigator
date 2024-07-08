package com.unisza.fiknavigator.utils

import com.unisza.fiknavigator.data.db.model.NodeConnection
import com.unisza.fiknavigator.data.model.PathNode
import java.util.PriorityQueue

data class Path(val nodes: List<Int>, val totalWeight: Int)

fun dijkstra(
    nodes: List<PathNode>,
    connections: List<NodeConnection>,
    startNodeId: Int,
    endNodeId: Int
): Path? {
    val distances = mutableMapOf<Int, Int>()
    val previousNodes = mutableMapOf<Int, Int?>()
    val unvisitedNodes = PriorityQueue(compareBy<Pair<Int, Int>> { it.second })

    // Initialize distances and previous nodes
    nodes.forEach {
        distances[it.nodeId!!] = Int.MAX_VALUE
        previousNodes[it.nodeId] = null
    }
    distances[startNodeId] = 0
    unvisitedNodes.add(Pair(startNodeId, 0))

    while (unvisitedNodes.isNotEmpty()) {
        val (currentNodeId, currentDistance) = unvisitedNodes.poll()!!

        // Early exit if we reach the end node
        if (currentNodeId == endNodeId) break

        connections.filter { it.node1 == currentNodeId || it.node2 == currentNodeId }.forEach { connection ->
            val neighborNodeId = if (connection.node1 == currentNodeId) connection.node2!! else connection.node1!!
            val newDistance = currentDistance + connection.weight!!

            if (newDistance < distances[neighborNodeId]!!) {
                distances[neighborNodeId] = newDistance
                previousNodes[neighborNodeId] = currentNodeId
                unvisitedNodes.add(Pair(neighborNodeId, newDistance))
            }
        }
    }

    // Reconstruct path
    val path = mutableListOf<Int>()
    var currentNode: Int? = endNodeId
    while (currentNode != null) {
        path.add(0, currentNode)
        currentNode = previousNodes[currentNode]
    }

    return if (path.first() == startNodeId) Path(path, distances[endNodeId]!!) else null
}