package com.cheeke.surfy.detail.series.navigation

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class SeriesScreen(val id: Int) : Screen

fun Navigator.goToSeries(id: Int) { goTo(screen = SeriesScreen(id = id)) }