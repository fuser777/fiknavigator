package com.unisza.fiknavigator.utils.map



import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Images original state
class TransformationStates {
    var scale by mutableFloatStateOf(1f)
    var rotation by mutableFloatStateOf(0f)
    var offset by mutableStateOf(Offset.Zero)
    var state: TransformableState = TransformableState { _, _, _ -> }
}

// Allow Image to be moved, zoomed, and rotated
@Composable
fun rememberTransformationStates(): TransformationStates {
    val transformationStates = remember { TransformationStates() }
    transformationStates.state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->

        // Zooming functions
        val newScale = (transformationStates.scale * zoomChange).coerceIn(0.1f, 10f)

        // Update scale
        transformationStates.scale = newScale

        // Update rotation
        transformationStates.rotation += rotationChange

        // Update offset
        val newOffset = transformationStates.offset.rotateBy(rotationChange * PI / 180) + offsetChange / newScale
        transformationStates.offset = newOffset
    }
    return transformationStates
}

// Extension function to rotate offset by angle
fun Offset.rotateBy(angle: Double): Offset {
    return Offset(
        (x * cos(angle) - y * sin(angle)).toFloat(),
        (x * sin(angle) + y * cos(angle)).toFloat()
    )
}