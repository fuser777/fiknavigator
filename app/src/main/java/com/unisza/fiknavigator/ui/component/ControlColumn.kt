package com.unisza.fiknavigator.ui.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.unisza.fiknavigator.data.preferences.PreferencesManager
import com.unisza.fiknavigator.ui.theme.clickedColor
import com.unisza.fiknavigator.ui.theme.downButtonColor
import com.unisza.fiknavigator.ui.theme.upButtonColor
import com.unisza.fiknavigator.ui.viewmodel.MapViewModel
import com.unisza.fiknavigator.utils.AssetManager
import com.unisza.fiknavigator.utils.map.TransformationStates

@Composable
fun ControlColumn(context: Context, transformationStates: TransformationStates, mapViewModel: MapViewModel) {
    val currentFloor = PreferencesManager.getCurrentFloorLevel(context)
    val floorList = AssetManager.getFloorsInDepartment(context).size
    var showDepartmentPopup by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // Upper floor button
        FloorSwitch("+", floorList - 1, currentFloor, upButtonColor) {
            updateFloorLevel(context, mapViewModel, transformationStates, minOf(currentFloor + 1, floorList - 1))
        }

        // Show current floor
        Text("FLOOR $currentFloor", modifier = Modifier.padding(8.dp))

        // Lower floor button
        FloorSwitch("-", 0, currentFloor, downButtonColor) {
            updateFloorLevel(context, mapViewModel, transformationStates, maxOf(currentFloor - 1, 0))
        }

        // Department selection button
        Button(
            onClick = { showDepartmentPopup = true },
            Modifier.padding(top = 5.dp)
        ) {
            Text(PreferencesManager.getCurrentDepartment(context).toString())
        }

        val textNotification by mapViewModel.notificationText.collectAsState()

        if(!textNotification.isNullOrEmpty()){
            // Show Next Direction
            Text(textNotification?:"UNKNOWN", modifier = Modifier.padding(8.dp))
        }

        if (showDepartmentPopup) {
            DepartmentSelectionPopup(
                context = context,
                mapViewModel = mapViewModel,
                transformationStates = transformationStates,
                onDismissRequest = { showDepartmentPopup = false },
                onDepartmentSelected = { selectedDepartment ->
                    PreferencesManager.setCurrentDepartment(context, selectedDepartment)
                    mapViewModel.updateFloorLevel(context, 0) // Reset to the first floor of the new department
                }
            )
        }
    }
}

@Composable
fun FloorSwitch(text: String, limit: Int, index: Int, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(if (index == limit) clickedColor else color),
        enabled = index != limit
    ) {
        Text(text)
    }
}

fun updateFloorLevel(context: Context, mapViewModel: MapViewModel, transformationStates: TransformationStates, newFloor: Int) {
    mapViewModel.updateFloorLevel(context, newFloor)
    mapViewModel.resetTransformationStates(transformationStates)
}

@Composable
fun DepartmentSelectionPopup(
    context: Context,
    mapViewModel: MapViewModel,
    transformationStates: TransformationStates,
    onDismissRequest: () -> Unit,
    onDepartmentSelected: (String) -> Unit) {

    val departments = AssetManager.getDepartmentsFolders(context)

    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            Modifier
                .background(Color.White)
                .padding(15.dp),
        ) {
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SELECT\nDEPARTMENT", Modifier.padding(bottom = 20.dp), fontSize = 20.sp, textAlign = TextAlign.Center)
                departments.forEach { department ->
                    Button(
                        onClick = {
                            onDepartmentSelected(department)
                            onDismissRequest()
                            mapViewModel.setCurrentDepartment(context, department)
                            mapViewModel.updateFloorLevel(context, 0)
                            mapViewModel.resetTransformationStates(transformationStates)
                            mapViewModel.updateMarkerSizeConfig(context, department)
                        },
                        enabled = department != PreferencesManager.getCurrentDepartment(context),
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Text(department)
                    }
                }
            }
        }
    }
}