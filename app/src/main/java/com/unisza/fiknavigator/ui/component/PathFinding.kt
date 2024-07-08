package com.unisza.fiknavigator.ui.component

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.unisza.fiknavigator.data.preferences.PreferencesManager
import com.unisza.fiknavigator.ui.viewmodel.MapViewModel

@Composable
fun PathFinding(context:Context, mapViewModel:MapViewModel, coordinateFactor: Float){
    // Load path nodes
    val pathNodes by mapViewModel.pathNodes.collectAsState()

    if (pathNodes.isNotEmpty()) {
        val startNode = pathNodes.firstOrNull()
        val endNode = pathNodes.lastOrNull()
        val currentDepartment = PreferencesManager.getCurrentDepartment(context)
        val currentFloor = PreferencesManager.getCurrentFloorLevel(context)

        Canvas(modifier = Modifier) {
            for (i in 0 until pathNodes.size - 1) {
                val start = pathNodes[i]
                val end = pathNodes[i + 1]

                if(endNode?.buildingCode != currentDepartment){ // If end node is not in same department
                    if(currentFloor != 0){ // Go down to move between building
                        mapViewModel.updateNotificationText(context,"Go to Down Floor")
                    }else{
                        if(start.nodeType == "right" && end.nodeType == "left"){
                            mapViewModel.updateNotificationText(context,"Go to\n${endNode?.buildingCode} Building\non Right")
                        }else if (start.nodeType == "left" && end.nodeType == "right"){
                            mapViewModel.updateNotificationText(context,"Go to\n${endNode?.buildingCode} Building\non Left")
                        }
                    }
                }else if(endNode?.buildingCode == currentDepartment){ // If end node is at same department
                    if(endNode?.z == currentFloor){
                        mapViewModel.updateNotificationText(context,"End Floor")
                    }else if(endNode?.z!! > start.z!!){ // If end node is at higher floor
                        mapViewModel.updateNotificationText(context,"Go to\nUpper Floor")
                    }else if(endNode.z < start.z){ // If end node is at lower floor
                        mapViewModel.updateNotificationText(context,"Go to\nDown Floor")
                    }
                }

                // Draw if the path is in the same building and floor level
                if ((end.buildingCode == currentDepartment) && (end.z == currentFloor)) {
                    // Draw if it's not a different building or stair to stair or lift to lift
                    if (!((start.nodeType == "right" && end.nodeType == "left") || (start.nodeType == "left" && end.nodeType == "right") || (start.nodeType == "stair" && end.nodeType == "stair") || (start.nodeType == "lift" && end.nodeType == "lift"))) {
                        drawLine(
                            color = Color.Red,
                            start = Offset(
                                start.x!! / coordinateFactor,
                                start.y!! / coordinateFactor
                            ),
                            end = Offset(end.x!! / coordinateFactor, end.y!! / coordinateFactor),
                            strokeWidth = 5f
                        )
                        PreferencesManager.setNotificationText(context = context, notificationText = "")
                    }
                }
            }
        }
    }
}