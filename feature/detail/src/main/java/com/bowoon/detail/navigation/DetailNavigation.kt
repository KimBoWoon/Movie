package com.bowoon.detail.navigation

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class Detail(val id: Int) : Screen

fun Navigator.goToMovie(id: Int) { goTo(Detail(id = id)) }