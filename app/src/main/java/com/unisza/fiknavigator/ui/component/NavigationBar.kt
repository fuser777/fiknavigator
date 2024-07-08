package com.unisza.fiknavigator.ui.component
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unisza.fiknavigator.ui.theme.clearAllLocationButtonColor
import com.unisza.fiknavigator.ui.theme.defaultLineColor
import com.unisza.fiknavigator.ui.theme.defaultTextColor
import com.unisza.fiknavigator.ui.theme.startNavigationButtonColor
import com.unisza.fiknavigator.ui.theme.uiColor
import com.unisza.fiknavigator.ui.viewmodel.MapViewModel

@Composable
fun NavigationBar(context: Context, mapViewModel: MapViewModel) {
    val startLocation by mapViewModel.startLocation.collectAsState()
    val endLocation by mapViewModel.endLocation.collectAsState()
    val isNavigationBarVisible by mapViewModel.isNavigationBarVisible.collectAsState()
    val startLocationName by mapViewModel.startLocationName.collectAsState()
    val endLocationName by mapViewModel.endLocationName.collectAsState()

    var showStartLocationRemoveDialog by remember { mutableStateOf(false) }
    var showEndLocationRemoveDialog by remember { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }
    var showStartNavigationDialog by remember { mutableStateOf(false) }

    if (isNavigationBarVisible) {
        Box(
            modifier = Modifier
                .background(uiColor)
                .wrapContentWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                if (startLocation != 0) {
                    TextButton(
                        onClick = { showStartLocationRemoveDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, defaultLineColor),
                        shape = RectangleShape
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Start Location: [$startLocation] ${startLocationName ?: "Unknown"}",
                                modifier = Modifier.weight(1f),
                                color = defaultTextColor
                            )
                        }
                    }
                    Text(
                        text = "Tap above to remove start location",
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                    Spacer(Modifier.padding(top = 16.dp))
                }
                if (endLocation != 0) {
                    TextButton(
                        onClick = { showEndLocationRemoveDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, defaultLineColor),
                        shape = RectangleShape
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "End Location: [$endLocation] ${endLocationName ?: "Unknown"}",
                                modifier = Modifier.weight(1f),
                                color = defaultTextColor
                            )
                        }
                    }
                    Text(
                        text = "Tap above to remove end location",
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = { showClearAllDialog = true },
                    Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = clearAllLocationButtonColor),
                ) {
                    Text(text = "Clear All Locations", color = Color.Black)
                }

                if (startLocation != 0 && endLocation != 0) {
                    Button(
                        onClick = { showStartNavigationDialog = true },
                        Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = startNavigationButtonColor),
                    ) {
                        Text("Start Navigation")
                    }
                }
            }
        }

        if (showStartLocationRemoveDialog) {
            AlertDialog(
                onDismissRequest = { showStartLocationRemoveDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mapViewModel.setStartLocation(context, 0)
                            showStartLocationRemoveDialog = false
                            mapViewModel.clearPath()
                        }
                    ) { Text("Remove Start Location") }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showStartLocationRemoveDialog = false }
                    ) { Text("Cancel") }
                },
                title = { Text("Remove Start Location") },
                text = { Text("Are you sure you want to remove the start location?") }
            )
        }

        if (showEndLocationRemoveDialog) {
            AlertDialog(
                onDismissRequest = { showEndLocationRemoveDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showEndLocationRemoveDialog = false
                            mapViewModel.setEndLocation(context, 0)
                            mapViewModel.clearPath()
                        }
                    ) { Text("Remove End Location") }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showEndLocationRemoveDialog = false }
                    ) { Text("Cancel") }
                },
                title = { Text("Remove End Location") },
                text = { Text("Are you sure you want to remove the end location?") }
            )
        }

        if (showClearAllDialog) {
            AlertDialog(
                onDismissRequest = { showClearAllDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearAllDialog = false
                            mapViewModel.clearAllLocations(context)
                        }
                    ) { Text("Clear All Locations") }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showClearAllDialog = false }
                    ) { Text("Cancel") }
                },
                title = { Text("Clear All Locations") },
                text = { Text("Are you sure you want to clear all locations?") }
            )
        }

        if (showStartNavigationDialog) {
            AlertDialog(
                onDismissRequest = { showStartNavigationDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Start navigation logic here
                            mapViewModel.startNavigation(context, startLocation, endLocation)
                            showStartNavigationDialog = false
                        }
                    ) { Text("Start Navigation") }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showStartNavigationDialog = false }
                    ) { Text("Cancel") }
                },
                title = { Text("Start Navigation") },
                text = { Text("Are you sure you want to start navigation?") }
            )
        }
    }
}
