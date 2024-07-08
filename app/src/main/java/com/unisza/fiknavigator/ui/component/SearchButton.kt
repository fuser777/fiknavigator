// SearchButton.kt
package com.unisza.fiknavigator.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.unisza.fiknavigator.R
import com.unisza.fiknavigator.ui.viewmodel.MapViewModel

@Composable
fun SearchButton(mapViewModel:MapViewModel,onClick: () -> Unit) {
    val isSearchButtonVisible = true // for future implementation
    if (isSearchButtonVisible){
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_searched_for),
                    contentDescription = "Search Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Search")
            }
        }
    }
}
