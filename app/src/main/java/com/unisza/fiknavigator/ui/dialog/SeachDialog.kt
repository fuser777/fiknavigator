package com.unisza.fiknavigator.ui.dialog

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.unisza.fiknavigator.data.model.SearchResult
import com.unisza.fiknavigator.ui.component.SearchResultItem
import com.unisza.fiknavigator.ui.viewmodel.MapViewModel
import com.unisza.fiknavigator.utils.FullscreenDialog
import com.unisza.fiknavigator.utils.map.TransformationStates

@Composable
fun SearchDialog(
    mapViewModel: MapViewModel,
    context: Context,
    transformationStates: TransformationStates,
    onDismissRequest: () -> Unit
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var searchResults by remember { mutableStateOf<List<SearchResult>>(emptyList()) }

    FullscreenDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Closing this dialog
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                    }

                    // Searching field
                    TextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            if (searchText.text.isEmpty()) {
                                searchResults = emptyList()
                            } else {
                                mapViewModel.searchNodes(searchText.text) { results ->
                                    // send value to results here
                                    searchResults = results
                                }
                            }
                        },
                        label = {Text("Search")},
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .background(Color.LightGray),
                        trailingIcon = {
                            if (searchText.text.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchText = TextFieldValue("")
                                    searchResults = emptyList()
                                }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear")
                                }
                            }
                        }
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(searchResults) { result ->
                        when (result) {
                            is SearchResult.NodeResult -> {
                                SearchResultItem(
                                    nodeName = result.resultNode.nodeName?: "UNKNOWN",
                                    floorLevel = result.resultNode.z?: 0,
                                    buildingCode = result.resultNode.buildingCode?: "UNKNOWN",
                                    onClick = {
                                        onDismissRequest()
                                        mapViewModel.showMarkerBottomSheet(nodeId = result.resultNode.nodeId?:0, context = context, transformationStates = transformationStates)
                                    }
                                )
                            }
                            is SearchResult.NodeWithAliasResult -> {
                                SearchResultItem(
                                    nodeName = result.resultAlias.roomAlias?: "UNKNOWN",
                                    floorLevel = result.resultAlias.z?: 0,
                                    buildingCode = result.resultAlias.buildingCode?: "UNKNOWN",
                                    onClick = {
                                        onDismissRequest()
                                        mapViewModel.showMarkerBottomSheet(nodeId = result.resultAlias.nodeId?:0, context = context, transformationStates = transformationStates)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}