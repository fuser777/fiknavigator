package com.unisza.fiknavigator.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.unisza.fiknavigator.utils.AssetManager

// PreferencesManager.kt
object PreferencesManager {
    private const val PREFERENCES_FILE_KEY = "com.unisza.fiknavigator.PREFERENCES"
    private const val KEY_CURRENT_DEPARTMENT = "KEY_CURRENT_DEPARTMENT"
    private const val KEY_CURRENT_FLOOR_LEVEL = "KEY_CURRENT_FLOOR_LEVEL"
    private const val KEY_CURRENT_START_LOCATION = "KEY_CURRENT_START_LOCATION"
    private const val KEY_CURRENT_END_LOCATION = "KEY_CURRENT_END_LOCATION"
    private const val KEY_NAVIGATION_PATH = "KEY_NAVIGATION_PATH"
    private const val KEY_CURRENT_NOTIFICATION = "KEY_CURRENT_NOTIFICATION"
    private const val ONBOARDING_COMPLETED_KEY = "ONBOARDING_COMPLETED"

    fun getCurrentDepartment(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        var currentDepartment = sharedPreferences.getString(KEY_CURRENT_DEPARTMENT, null)
        currentDepartment = currentDepartment ?: AssetManager.getDepartmentsFolders(context).firstOrNull()
        return sharedPreferences.getString(KEY_CURRENT_DEPARTMENT, currentDepartment)
    }

    fun setCurrentDepartment(context: Context, department: String?) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(KEY_CURRENT_DEPARTMENT, department)
            apply()
        }
    }

    fun getCurrentFloorLevel(context: Context): Int {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(KEY_CURRENT_FLOOR_LEVEL, 0)
    }

    fun setCurrentFloorLevel(context: Context, floorLevel: Int) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt(KEY_CURRENT_FLOOR_LEVEL, floorLevel)
            apply()
        }
    }

    fun getCurrentStartLocation(context: Context): Int {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(KEY_CURRENT_START_LOCATION, 0)
    }

    fun setCurrentStartLocation(context: Context, startLocation: Int) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt(KEY_CURRENT_START_LOCATION, startLocation)
            apply()
        }
    }

    fun getCurrentEndLocation(context: Context): Int {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(KEY_CURRENT_END_LOCATION, 0)
    }

    fun setCurrentEndLocation(context: Context, endLocation: Int) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt(KEY_CURRENT_END_LOCATION, endLocation)
            apply()
        }
    }

    fun setNavigationPath(context: Context, path: List<Int>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        val pathString = path.joinToString(separator = ",")
        with(sharedPreferences.edit()) {
            putString(KEY_NAVIGATION_PATH, pathString)
            apply()
        }
    }

    fun clearNavigationPath(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove(KEY_NAVIGATION_PATH)
            apply()
        }
    }

    fun getNotificationText(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_CURRENT_NOTIFICATION, "")
    }

    fun setNotificationText(context: Context, notificationText: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(KEY_CURRENT_NOTIFICATION, notificationText)
            apply()
        }
    }

    fun setOnboardingCompleted(context: Context, completed: Boolean) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(ONBOARDING_COMPLETED_KEY, completed)
            apply()
        }
    }

    fun isOnboardingCompleted(context: Context): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(ONBOARDING_COMPLETED_KEY, false)
    }

//
//    fun getNavigationPath(context: Context): List<Int> {
//        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
//        val pathString = sharedPreferences.getString(KEY_NAVIGATION_PATH, null) ?: return emptyList()
//        return pathString.split(",").mapNotNull { it.toIntOrNull() }
//    }
//
}
