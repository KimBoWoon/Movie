package com.bowoon.series.navigation

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class Series(val id: Int) : Screen

fun Navigator.goToSeries(id: Int) { goTo(Series(id = id)) }