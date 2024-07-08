package com.unisza.fiknavigator.ui.component

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.unisza.fiknavigator.data.db.model.Node
import com.unisza.fiknavigator.ui.viewmodel.MapViewModel
import com.unisza.fiknavigator.utils.AssetManager
import com.unisza.fiknavigator.utils.map.TransformationStates
import kotlin.math.min

@Composable
fun TransformableBox(
    context: Context,
    transformationStates: TransformationStates,
    markers: List<Node>,
    loading: Boolean,
    onImageLoaded: () -> Unit,
    mapViewModel: MapViewModel
) {
    // Metrics of Screen
    val displayMetrics = context.resources.displayMetrics

    // Width And Height Of Screen
    val deviceWidth = displayMetrics.widthPixels
    val deviceHeight = displayMetrics.heightPixels

    Box( // Box 1
        modifier = Modifier
            .background(Color.White)
            // Define max size
            .requiredSize(deviceWidth.dp, deviceWidth.dp)
            .graphicsLayer(
                scaleX = transformationStates.scale,
                scaleY = transformationStates.scale,
                rotationZ = transformationStates.rotation,
                translationX = transformationStates.offset.x * transformationStates.scale,
                translationY = transformationStates.offset.y * transformationStates.scale
            )
        , contentAlignment = Alignment.Center,
    ) {
        Box( // Box 2
            modifier = Modifier
                .background(Color.White)
                .requiredSize(deviceWidth.dp, deviceHeight.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Offset of current image
            var imageCurrentOffset by remember {
                mutableStateOf(Offset(0f, 0f))
            }

            // Factor to scale image out so it fits width of device
            var scaleFactor by remember {
                mutableFloatStateOf(0f)
            }

            // For original image width and height
            val (imageWidth, imageHeight) = context.assets.open(AssetManager.getFloorPath(context))
                .use { inputStream ->
                    BitmapFactory.Options().run {
                        inJustDecodeBounds = true
                        BitmapFactory.decodeStream(inputStream, null, this)
                        outWidth to outHeight
                    }
                }

            // For marker positioning
            var coordinateFactor by remember {
                mutableFloatStateOf(0f)
            }

            Box( //Box 3
                modifier = Modifier
                    .background(Color.White)
                    // Movable box
                    .onSizeChanged {
                        // Get current Image Height and Width
                        imageCurrentOffset = Offset(it.width.toFloat(), it.height.toFloat())

                        // To zoom out and the image would be seen
                        scaleFactor = min(
                            deviceWidth / imageCurrentOffset.x,
                            deviceHeight / imageCurrentOffset.y
                        )

                        //Factor for marker placement
                        coordinateFactor = min(
                            imageWidth / imageCurrentOffset.x,
                            imageHeight / imageCurrentOffset.y
                        )
                    }
                    // Scaling the box
                    .graphicsLayer(
                        // Scale out the image so it fits width of the screen
                        scaleX = scaleFactor,
                        scaleY = scaleFactor
                    )
            ) {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data("file:///android_asset/${AssetManager.getFloorPath(context)}")
                        .crossfade(true)
                        .build()
                )
                val painterState = painter.state

                if (painterState is AsyncImagePainter.State.Success) {
                    onImageLoaded()
                }

                Image(
                    painter = painter,
                    contentDescription = "Floor Image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .background(Color.White)
                )

                // Load marker only if the image has finished loading
                if (!loading && painterState is AsyncImagePainter.State.Success) {
                    markers.forEachIndexed { _, marker ->
                        MarkerButton(
                            marker = marker,
                            factor = coordinateFactor,
                            context = context,
                            transformationStates = transformationStates,
                            mapViewModel = mapViewModel
                        )
                    }

                    PathFinding(context = context, mapViewModel = mapViewModel, coordinateFactor = coordinateFactor)
                }
            }
        }
    }
}