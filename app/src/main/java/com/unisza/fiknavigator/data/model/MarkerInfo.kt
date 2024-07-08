package com.unisza.fiknavigator.data.model

sealed class MarkerInfo {
    data class NodeInfo(
        val nodeId: Int?,
        val x: Int?,
        val y: Int?,
        val z: Int?,
        val nodeType: String?,
        val nodeName: String?,
        val building: String?,
        val roomAlias: List<String>?
    ) : MarkerInfo()
}
