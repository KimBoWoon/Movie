package com.cheeke.surfy.detail.tv.navigation

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class TvScreen(val id: Int) : Screen

fun Navigator.goToTv(id: Int) { goTo(screen = TvScreen(id = id)) }