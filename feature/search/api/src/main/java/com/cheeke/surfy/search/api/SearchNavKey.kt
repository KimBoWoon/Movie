package com.cheeke.surfy.search.api

import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.model.SearchType
import kotlinx.serialization.Serializable

@Serializable
data class SearchNavKey(
    val query: String = "",
    val searchType: SearchType = SearchType.MULTI
) : NavKey
