package com.unisza.fiknavigator.ui.modal

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unisza.fiknavigator.data.model.MarkerInfo
import com.unisza.fiknavigator.data.preferences.PreferencesManager
import com.unisza.fiknavigator.ui.theme.defaultLineColor
import com.unisza.fiknavigator.ui.theme.resetButtonColor
import com.unisza.fiknavigator.ui.theme.uiColor
import com.unisza.fiknavigator.ui.viewmodel.MapViewModel

@Composable
fun MarkerInfo(
    context: Context,
    markerInfo: MarkerInfo,
    mapViewModel: MapViewModel,
    onDismissRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(uiColor, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(16.dp)
            .clickable(enabled = false) {} // Prevent clicks from propagating
    ) {
        markerInfo.let { it ->
            if (it is MarkerInfo.NodeInfo) {

                // Title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, defaultLineColor)
                        .padding(vertical = 10.dp)
                    , contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it.nodeName ?: "Unknown",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // Details sections
                Box(modifier = Modifier.border(1.dp, defaultLineColor)){
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ){
                        markerInfo.let {
                            if (it is MarkerInfo.NodeInfo) {
                                Text("Marker ID: ${it.nodeId ?: 0}")
                                Text("Marker Type: ${it.nodeType ?: "Unknown"}")
                                Text("Floor Level: ${it.z ?: "Unknown"}")
                                Text("Department: ${it.building ?: "Unknown"}")
                                Text("Room Aliases: ${it.roomAlias?.joinToString(", ") ?: "None"}")
                            }
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center // Center the buttons
        ) {
            markerInfo.let {
                var startLocation by remember { mutableIntStateOf(PreferencesManager.getCurrentStartLocation(context)) }
                var endLocation by remember { mutableIntStateOf(PreferencesManager.getCurrentEndLocation(context)) }
                if (it is MarkerInfo.NodeInfo) {
                    // Both buttons are disabled if already set
                    Button(
                        enabled = (
                                startLocation == 0 && endLocation != it.nodeId)  // if start location is 0 (default) and end location is not this node = setting new start location
                                ||
                                startLocation == it.nodeId, // if start location is this node = to reset
                        onClick = {
                            val newStartLocation = if (startLocation != it.nodeId) it.nodeId ?: 0 else 0
                            mapViewModel.setStartLocation(context, newStartLocation)
                            startLocation = newStartLocation
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = (if (startLocation == it.nodeId) resetButtonColor else Color.Unspecified)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(" ${
                            when (startLocation) {
                                0 -> {"Set Start Location"}
                                it.nodeId -> {"Reset Start Location"}
                                else -> {"Start: $startLocation"}
                            }
                        }")
                    }

                    Button(
                        enabled = (endLocation == 0 && startLocation != it.nodeId) // if end location is 0 (default) and end location is not this node = setting new start location
                                ||
                                endLocation == it.nodeId, // if end location is this node = to reset
                        onClick = {
                            val newEndLocation = if (endLocation != it.nodeId) it.nodeId ?: 0 else 0
                            mapViewModel.setEndLocation(context, newEndLocation)
                            endLocation = newEndLocation
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = (if (endLocation == it.nodeId) resetButtonColor else Color.Unspecified)),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(" ${
                            when (endLocation) {
                                0 -> {"Set End Location"}
                                it.nodeId -> {"Reset End Location"}
                                else -> {"End: $endLocation"}
                            }
                        }")
                    }
                }
            }
        }
    }
}
