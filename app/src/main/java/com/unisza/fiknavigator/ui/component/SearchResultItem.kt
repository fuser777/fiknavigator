package com.unisza.fiknavigator.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unisza.fiknavigator.ui.theme.defaultLineColor
import com.unisza.fiknavigator.ui.theme.defaultTextColor

@Composable
fun SearchResultItem(
    nodeName: String,
    floorLevel: Int,
    buildingCode: String,
    onClick: () -> Unit
) {
   Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .padding(10.dp)
                .border(1.dp, defaultLineColor)  // Add border
                .padding(10.dp)  // Add padding inside the border
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$nodeName - F:$floorLevel - $buildingCode",
                    style = TextStyle(color = defaultTextColor, fontSize = 16.sp),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
}

