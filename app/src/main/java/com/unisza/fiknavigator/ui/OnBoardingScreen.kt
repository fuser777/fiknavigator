package com.unisza.fiknavigator.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.unisza.fiknavigator.R
import com.unisza.fiknavigator.data.preferences.PreferencesManager.setOnboardingCompleted
import com.unisza.fiknavigator.ui.theme.defaultTextColor
import com.unisza.fiknavigator.ui.theme.resetButtonColor
import com.unisza.fiknavigator.ui.theme.uiColor
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val onboardingPages = listOf(
        OnboardingPage(
            imageResId = R.drawable.logo_app,
            text = "Welcome to FIK Navigator! This app is designed to help FIK students navigate campus buildings with ease."
        ),
        OnboardingPage(
            imageResId = R.drawable.onboardingfloor,
            text = "Use Control Column to switch between different floors and buildings."
        ),
        OnboardingPage(
            imageResId = R.drawable.onboardingsearch,
            text = "Use Search Bar to easily search for any room within the FIK and KAP buildings."
        ),
        OnboardingPage(
            imageResId = R.drawable.onboardingmarkerinfo,
            text = "Tap on a search result or a marker on the map to view detailed room information."
        ),
        OnboardingPage(
            imageResId = R.drawable.onboardingnavigation,
            text = "Select your start and end locations, then use the Start Navigation button on Navigation Bar to begin your journey."
        ),
        OnboardingPage(
            imageResId = R.drawable.onboardingpath,
            text = "Once the route is calculated, it will be displayed on the map. Tap to hide other UI elements and view the map only."
        ),
        OnboardingPage(
            imageResId = R.drawable.logo_app,
            text = "That's it for the tutorial! Hope you enjoy using the FIK Navigator app."
        )
    )

    val pageIndex = remember{ mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(uiColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (pageIndex.intValue < onboardingPages.size) {
            val page = onboardingPages[pageIndex.intValue]
            OnboardingPageContent(page)

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(colors = ButtonDefaults.buttonColors(defaultTextColor),onClick = {
                    if (pageIndex.intValue >= 0){
                        pageIndex.intValue--
                    }
                }) {
                    Text("Back",color = Color.White)
                }

                Button(colors = ButtonDefaults.buttonColors(resetButtonColor),onClick = {
                    scope.launch {
                        setOnboardingCompleted(navController.context, true)
                        navController.navigate("map_screen")
                    }
                }) {
                    Text("Skip", color = Color.White)
                }

                Button(colors = ButtonDefaults.buttonColors(defaultTextColor),onClick = {
                    pageIndex.intValue++
                    if (pageIndex.intValue >= onboardingPages.size) {
                        scope.launch {
                            setOnboardingCompleted(navController.context, true)
                            navController.navigate("map_screen")
                        }
                    }
                }) {
                    Text("Next",color = Color.White)
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Image(
                painter = painterResource(id = page.imageResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                contentScale = ContentScale.Fit
            )
            Text(
                text = page.text,
                style = MaterialTheme.typography.headlineSmall,
                color = defaultTextColor,
                fontSize = 20.sp,
                maxLines = 5,
            )
        }
    }
}

data class OnboardingPage(val imageResId: Int, val text: String)