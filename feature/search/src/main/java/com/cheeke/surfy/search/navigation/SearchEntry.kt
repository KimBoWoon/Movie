package com.cheeke.surfy.search.navigation

import com.cheeke.surfy.model.SearchType
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchScreen(val query: String = "", val searchType: SearchType = SearchType.MULTI) : Screen