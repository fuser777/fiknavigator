package com.unisza.fiknavigator.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unisza.fiknavigator.data.model.MarkerInfo
import com.unisza.fiknavigator.ui.component.ControlColumn
import com.unisza.fiknavigator.ui.component.NavigationBar
import com.unisza.fiknavigator.ui.component.SearchButton
import com.unisza.fiknavigator.ui.component.TransformableBox
import com.unisza.fiknavigator.ui.dialog.SearchDialog
import com.unisza.fiknavigator.ui.modal.MarkerInfo
import com.unisza.fiknavigator.ui.theme.uiColor
import com.unisza.fiknavigator.ui.viewmodel.MapViewModel
import com.unisza.fiknavigator.ui.viewmodel.MapViewModelFactory
import com.unisza.fiknavigator.utils.map.rememberTransformationStates

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val factory = MapViewModelFactory(context)
    val mapViewModel: MapViewModel = viewModel(factory = factory)
    val transformationStates = rememberTransformationStates()
    val markers by mapViewModel.markers.collectAsState()
    val loading by mapViewModel.loading.collectAsState()

    var showSearchDialog by remember { mutableStateOf(false) }

    var showControlColumn by remember { mutableStateOf(true) }
    var showSearchBar by remember { mutableStateOf(true) }
    var showNavigationBar by remember { mutableStateOf(true) }

    val showMarker by mapViewModel.showMarkerInfo.observeAsState()

    Box(
        Modifier
            .fillMaxSize()
            .transformable(state = transformationStates.state)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // Toggle the visibility states on each tap
                        showControlColumn = !showControlColumn
                        showNavigationBar = !showNavigationBar
                        showSearchBar = !showSearchBar
                        if (showSearchDialog) {
                            showSearchDialog = false
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Box for moving the Images
        TransformableBox(
            context = context,
            transformationStates = transformationStates,
            markers = markers,
            loading = loading,
            onImageLoaded = { mapViewModel.setLoading(false) },
            mapViewModel = mapViewModel
        )

        Column(modifier = Modifier
            .fillMaxSize()
        ){
            // SearchBar
            AnimatedVisibility(
                visible = showSearchBar,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(500)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(500)
                )
            ) {
                // SearchBar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    SearchButton(mapViewModel= mapViewModel,onClick = { showSearchDialog = true })
                }
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ){
                // Navigation Bar
                AnimatedVisibility(modifier = Modifier.weight(1f),
                    visible = showControlColumn,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it }, // Enter from right
                        animationSpec = tween(500)
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX =  { -it }, // Exit to left
                        animationSpec = tween(500)
                    )
                ) {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(16.dp)),
                        ) {
                        NavigationBar(context = context, mapViewModel = mapViewModel)
                    }
                }

                // ControlColumn
                AnimatedVisibility(modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 16.dp),
                    visible = showControlColumn,
                    enter = slideInHorizontally(
                        initialOffsetX = { it }, // Enter from left
                        animationSpec = tween(500)
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it }, // Exit to right
                        animationSpec = tween(500)
                    )
                ) {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(uiColor)
                        .align(Alignment.Bottom), contentAlignment = Alignment.Center) {
                        ControlColumn(context, transformationStates, mapViewModel)
                    }
                }
            }
        }

        // SearchBar Dialog show
        if (showSearchDialog) {
            SearchDialog(
                mapViewModel = mapViewModel,
                context = context,
                transformationStates = transformationStates
            ) { showSearchDialog = false }
        }

        // Show Bottom Modal of Marker Info
        if (showMarker != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .pointerInput(Unit) {
                        detectTapGestures {
                            mapViewModel.showMarkerInfo.value = null
                        }
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                MarkerBottomSheetWithAnimation(
                    context = context,
                    mapViewModel = mapViewModel,
                    markerInfo = showMarker!!,
                    onDismissRequest = {
                        mapViewModel.showMarkerInfo.value = null
                    }
                )
            }
        }
    }
}

// Animation of Bottom Sheet
@Composable
fun MarkerBottomSheetWithAnimation(
    context: Context,
    mapViewModel: MapViewModel,
    markerInfo: MarkerInfo,
    onDismissRequest: () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(500)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(500)
        )
    ) {
        MarkerInfo(
            context = context,
            mapViewModel = mapViewModel,
            markerInfo = markerInfo,
            onDismissRequest = onDismissRequest
        )
    }
}