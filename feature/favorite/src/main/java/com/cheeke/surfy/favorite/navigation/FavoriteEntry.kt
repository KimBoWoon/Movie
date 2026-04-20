package com.cheeke.surfy.favorite.navigation

import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class FavoriteScreen(val tab: Int = 0) : Screen