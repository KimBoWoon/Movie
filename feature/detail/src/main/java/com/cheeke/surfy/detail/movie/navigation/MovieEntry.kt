package com.cheeke.surfy.detail.movie.navigation

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieScreen(val id: Int) : Screen

fun Navigator.goToMovie(id: Int) { goTo(screen = MovieScreen(id = id)) }