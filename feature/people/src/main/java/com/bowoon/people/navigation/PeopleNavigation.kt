package com.bowoon.people.navigation

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class People(val id: Int) : Screen

fun Navigator.goToPeople(id: Int) { goTo(People(id = id) )}