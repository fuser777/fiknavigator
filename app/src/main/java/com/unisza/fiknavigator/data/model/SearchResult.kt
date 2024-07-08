package com.unisza.fiknavigator.data.model

sealed class SearchResult {
    data class NodeResult(val resultNode: ResultNode) : SearchResult()
    data class NodeWithAliasResult(val resultAlias: ResultAlias) : SearchResult()
}
