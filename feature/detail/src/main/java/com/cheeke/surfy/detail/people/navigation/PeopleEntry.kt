package com.cheeke.surfy.detail.people.navigation

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class PeopleScreen(val id: Int) : Screen

fun Navigator.goToPeople(id: Int) { goTo(screen = PeopleScreen(id = id)) }