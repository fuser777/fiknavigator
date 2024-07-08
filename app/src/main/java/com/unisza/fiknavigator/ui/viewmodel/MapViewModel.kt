package com.unisza.fiknavigator.ui.viewmodel

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unisza.fiknavigator.data.config.departmentSizeConfigs
import com.unisza.fiknavigator.data.db.AppDatabase
import com.unisza.fiknavigator.data.db.model.Node
import com.unisza.fiknavigator.data.model.MarkerInfo
import com.unisza.fiknavigator.data.model.PathNode
import com.unisza.fiknavigator.data.model.SearchResult
import com.unisza.fiknavigator.data.preferences.PreferencesManager
import com.unisza.fiknavigator.utils.dijkstra
import com.unisza.fiknavigator.utils.map.TransformationStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(context: Context) : ViewModel() {
    private val db = AppDatabase.getDatabase(context)

    private val _markers = MutableStateFlow<List<Node>>(emptyList())
    val markers: StateFlow<List<Node>> get() = _markers

    private val _currentDepartment = MutableStateFlow(PreferencesManager.getCurrentDepartment(context))
    //private val currentDepartment: MutableStateFlow<String?> get() = _currentDepartment

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> get() = _loading

    var showMarkerInfo: MutableLiveData<MarkerInfo?> = MutableLiveData(null)

    val fontSize = MutableStateFlow(0.sp)
    val buttonWidth = MutableStateFlow(0.dp)

    // Start and End Location for Pathfinding
    val startLocation = MutableStateFlow(PreferencesManager.getCurrentStartLocation(context))
    val endLocation = MutableStateFlow(PreferencesManager.getCurrentEndLocation(context))

    // Navigation bar visibility
    private val _isNavigationBarVisible = MutableStateFlow(false)
    val isNavigationBarVisible: StateFlow<Boolean> get() = _isNavigationBarVisible

    private val _startLocationName = MutableStateFlow<String?>(null)
    val startLocationName: StateFlow<String?> get() = _startLocationName

    private val _endLocationName = MutableStateFlow<String?>(null)
    val endLocationName: StateFlow<String?> get() = _endLocationName

    private val _notificationText = MutableStateFlow(PreferencesManager.getNotificationText(context))
    val notificationText: MutableStateFlow<String?> get() = _notificationText

    // Initialization of the app
    init {
        val floorLevel = PreferencesManager.getCurrentFloorLevel(context)
        updateMarkerSizeConfig(context, PreferencesManager.getCurrentDepartment(context).toString())
        loadMarkers(context, floorLevel)
        updateNavigationBarVisibility()
        clearNotificationText()
        updateLocationName(startLocation.value, true)
        updateLocationName(endLocation.value, false)
    }

    fun updateFloorLevel(context: Context, newFloorLevel: Int) {
        // Clear markers immediately to prevent showing nodes from the old floor
        _markers.value = emptyList()
        _loading.value = true

        //Update new floor and the image would be recomposed automatically
        PreferencesManager.setCurrentFloorLevel(context, newFloorLevel)

        // Load the markers for current floor
        loadMarkers(context, newFloorLevel)
    }

    fun setCurrentDepartment(context:Context, department: String) {
        _currentDepartment.value = department
        PreferencesManager.setCurrentDepartment(context, department)
    }

    fun updateMarkerSizeConfig(context: Context, department: String) {
        val sizeConfig = departmentSizeConfigs[department] ?: departmentSizeConfigs["default"]!!
        val configuration = context.resources.configuration
        val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

        fontSize.value = if (isPortrait) sizeConfig.portraitFontSize else sizeConfig.landscapeFontSize
        buttonWidth.value = if (isPortrait) sizeConfig.portraitWidth else sizeConfig.landscapeWidth
    }

    // Load the current floor markers
    private fun loadMarkers(context: Context, floorLevel: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val department = db.buildingDao().getBuildingIdWithBuildingCode(PreferencesManager.getCurrentDepartment(context))
            val marker = db.nodeDao().getNodesWithFloorLevelAndDepartmentId(floorLevel, department)
            _markers.value = marker
        }
    }

    // Set Images state to original
    fun resetTransformationStates(transformationStates: TransformationStates) {
        transformationStates.scale = 1f
        transformationStates.offset = Offset.Zero
        transformationStates.rotation = 0f
    }

    // Set loading state
    fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    // Search functions
    fun searchNodes(query: String, onResult: (List<SearchResult>) -> Unit) {
        viewModelScope.launch {
            val nodes = db.nodeDao().getResultNodesWithName().filter {
                it.nodeName?.contains(query, ignoreCase = true) ?: false
            }
            val nodesWithAlias = db.nodeDao().getResultNodesWithRoomAlias().filter {
                it.roomAlias?.contains(query, ignoreCase = true) ?: false
            }
            val results = nodes.map { SearchResult.NodeResult(it) } + nodesWithAlias.map { SearchResult.NodeWithAliasResult(it) }
            onResult(results)
        }
    }

    fun showMarkerBottomSheet(nodeId: Int, context: Context, transformationStates: TransformationStates) {
        viewModelScope.launch {
            val node = withContext(Dispatchers.IO) { db.nodeDao().getNodeWithNodeId(nodeId).firstOrNull() }
            val roomAliases = withContext(Dispatchers.IO) { db.roomDao().getRoomAliasesWithNodeId(nodeId) }
            val markerInfo = node?.let { it ->
                MarkerInfo.NodeInfo(
                    nodeId = it.nodeId,
                    x = it.x,
                    y = it.y,
                    z = it.z,
                    nodeType = it.nodeType,
                    nodeName = it.nodeName,
                    building = it.buildingCode,
                    roomAlias = roomAliases.takeIf { it.isNotEmpty() }
                )
            }
            showMarkerInfo.value = markerInfo

            // Update floor and department if needed
            if (PreferencesManager.getCurrentFloorLevel(context) != node?.z || PreferencesManager.getCurrentDepartment(context) != node.buildingCode) {
                PreferencesManager.setCurrentDepartment(context, node?.buildingCode)
                updateFloorLevel(context, node?.z ?: 0)
                // Reset to default state
                resetTransformationStates(transformationStates)
            }
        }
    }

    // Reset Pathfinding Location
    private fun resetLocations(context: Context) {
        PreferencesManager.setCurrentStartLocation(context, 0)
        PreferencesManager.setCurrentEndLocation(context, 0)
        PreferencesManager.clearNavigationPath(context)
        startLocation.value = 0
        endLocation.value = 0
        _isNavigationBarVisible.value = false
    }

    // Function to update start location and navigation bar visibility
    fun setStartLocation(context: Context, location: Int) {
        PreferencesManager.setCurrentStartLocation(context, location)
        startLocation.value = location
        updateNavigationBarVisibility()
        updateLocationName(location, true)
    }

    // Function to update end location and navigation bar visibility
    fun setEndLocation(context: Context, location: Int) {
        PreferencesManager.setCurrentEndLocation(context, location)
        endLocation.value = location
        updateNavigationBarVisibility()
        updateLocationName(location, false)
    }

    // Function to update the node names of the start and end locations
    private fun updateLocationName(location: Int, isStartLocation: Boolean) {
        viewModelScope.launch {
            val nodeName = withContext(Dispatchers.IO) {
                db.nodeDao().getNodeNameWithNodeId(location)
            }
            if (isStartLocation) {
                _startLocationName.value = nodeName
            } else {
                _endLocationName.value = nodeName
            }
        }
    }

    // Function to show or hide navigation bar
    private fun updateNavigationBarVisibility() {
        _isNavigationBarVisible.value = startLocation.value != 0 || endLocation.value != 0
    }

    // PathFinding With Dijkstra starts here

    // Add StateFlows to manage path
    private val _pathNodes = MutableStateFlow<List<PathNode>>(emptyList())
    val pathNodes: StateFlow<List<PathNode>> get() = _pathNodes

    // Add function to start navigation
    fun startNavigation(context: Context, startNodeId: Int, endNodeId: Int) {
        viewModelScope.launch {
            val nodeConnections = withContext(Dispatchers.IO) { db.nodeConnectionDao().getAllConnections()}
            val pathNodeInfo = withContext(Dispatchers.IO) { db.nodeDao().getPathNodes()}
            val shortestPath = dijkstra(pathNodeInfo, nodeConnections, startNodeId, endNodeId)
            shortestPath?.let { path ->
                PreferencesManager.setNavigationPath(context, path.nodes)
                loadPathNodes(context = context, nodeIds = path.nodes)
            }
        }
    }

    // Add function to load path nodes
    private fun loadPathNodes(context: Context, nodeIds: List<Int>) {
        viewModelScope.launch {
            val pathNodes = withContext(Dispatchers.IO) {nodeIds.flatMap { nodeId -> db.nodeDao().getNodeInfo(nodeId) }}
            _pathNodes.value = pathNodes
            // Move the map screen to the start node
            PreferencesManager.setCurrentDepartment(context, pathNodes[0].buildingCode)
            updateFloorLevel(context,pathNodes[0].z?:0)
        }
    }

    fun clearAllLocations(context: Context) {
        resetLocations(context)
        clearPath()
        clearNotificationText()
    }

    fun clearPath(){
        _pathNodes.value = emptyList()
        clearNotificationText()
    }

    fun updateNotificationText(context: Context, notificationText: String){
        _notificationText.value = notificationText
        PreferencesManager.setNotificationText(context, notificationText)
    }

    private fun clearNotificationText(){
        _notificationText.value = ""
    }
}