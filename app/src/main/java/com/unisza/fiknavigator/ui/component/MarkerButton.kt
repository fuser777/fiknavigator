package com.unisza.fiknavigator.ui.component

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.unisza.fiknavigator.R
import com.unisza.fiknavigator.data.db.model.Node
import com.unisza.fiknavigator.ui.viewmodel.MapViewModel
import com.unisza.fiknavigator.utils.map.TransformationStates
import kotlin.math.roundToInt

@Composable
fun MarkerButton(marker: Node, factor: Float, context: Context, transformationStates: TransformationStates, mapViewModel: MapViewModel) {
    // Offset of current box
    var buttonOffset by remember { mutableStateOf(Offset(0f, 0f)) }

    // Observe fontSize and buttonWidth from ViewModel
    val fontSize by mapViewModel.fontSize.collectAsState()
    val buttonWidth by mapViewModel.buttonWidth.collectAsState()

    // Determine which icon to show based on node type and name
    val iconResource: Int? = when (marker.nodeType) {
        "right" -> R.drawable.mrk_right
        "left" -> R.drawable.mrk_left
        "wc_f" -> R.drawable.mrk_wc_f
        "wc_m" -> R.drawable.mrk_wc_m
        "wc_oku" -> R.drawable.mrk_wc_oku
        "lift" -> R.drawable.mrk_lift
        "surau" -> R.drawable.mrk_mosque
        "stair" -> when {
            marker.nodeName?.endsWith("U") == true -> R.drawable.mrk_stair_up
            marker.nodeName?.endsWith("D") == true -> R.drawable.mrk_stair_down
            else -> R.drawable.mrk_stair
        }
        else -> null
    }

    Box(
        modifier = Modifier
            // Get the button coordinates
            .onSizeChanged { buttonOffset = Offset(it.width.toFloat(), it.height.toFloat()) }
            .offset {
                IntOffset(
                    // Centering the button on the coordinates
                    ((marker.x?.div(factor))?.minus(buttonOffset.x / 2))?.roundToInt() ?: 0,
                    ((marker.y?.div(factor))?.minus(buttonOffset.y / 2))?.roundToInt() ?: 0,
                )
            }
            .graphicsLayer { rotationZ =
                if(!(marker.nodeType == "right" || marker.nodeType == "left")) -transformationStates.rotation //Make the marker rotate in reverse so that use can read the text properly
                else 0f // The right and left arrow icons are not moved
            }
            .background(Color.White)
            .clickable {
                // Show Marker Box here based on the marker_id of the clicked marker
                marker.nodeId?.let { mapViewModel.showMarkerBottomSheet(nodeId = it, context = context, transformationStates = transformationStates) }
            },
        contentAlignment = Alignment.Center
    ) {
        iconResource?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = marker.nodeName ?: "",
                modifier = Modifier.size(10.dp)
            )
        } ?: run {
            Text(
                text = marker.nodeName ?: "",
                color = Color.Black,
                modifier = Modifier
                    .width(buttonWidth)  // Adjust this value
                    .wrapContentHeight(),
                fontSize = fontSize,  // Adjust this value
                overflow = TextOverflow.Ellipsis,
                maxLines = 10,
                textAlign = TextAlign.Center
            )
        }
    }
}
