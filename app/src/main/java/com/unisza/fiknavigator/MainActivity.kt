package com.unisza.fiknavigator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unisza.fiknavigator.data.preferences.PreferencesManager
import com.unisza.fiknavigator.ui.MapScreen
import com.unisza.fiknavigator.ui.OnboardingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val startDestination = remember {
        if (PreferencesManager.isOnboardingCompleted(navController.context)) "map_screen" else "onboarding_screen"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding_screen") { OnboardingScreen(navController) }
        composable("map_screen") { MapScreen() }
    }
}