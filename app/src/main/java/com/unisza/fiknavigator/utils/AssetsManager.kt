package com.unisza.fiknavigator.utils

import android.content.Context
import com.unisza.fiknavigator.data.preferences.PreferencesManager

object AssetManager {

    // List department name folders in tiles/
    fun getDepartmentsFolders(context: Context): List<String> {
        return context.assets.list("tiles")?.toList() ?: emptyList()
    }

    // List all floors in the tiles/{department}/ directory
    fun getFloorsInDepartment(context: Context): List<String> {
        val currentDepartment = PreferencesManager.getCurrentDepartment(context) ?: ""
        return context.assets.list("tiles/$currentDepartment")?.toList() ?: emptyList()
    }

    // Get the file path to the image in by system path file:///android_asset/tiles/{department}/
    fun getFloorPath(context: Context): String {
        // Get the current department name
        val currentDepartment = PreferencesManager.getCurrentDepartment(context) ?: ""
        val currentFloor = PreferencesManager.getCurrentFloorLevel(context)

        // Return the path for file
        return "tiles/$currentDepartment/${getFloorsInDepartment(context)[currentFloor]}"
    }
}
