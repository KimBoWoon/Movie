package com.bowoon.my.navigation

import androidx.annotation.Keep
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.bowoon.my.MyScreen
import kotlinx.serialization.Serializable

@Serializable
@Keep
data object MyRoute

fun NavController.navigateToMy(navOptions: NavOptions) = navigate(route = MyRoute, navOptions)

fun NavGraphBuilder.myScreen() {
    composable<MyRoute>() {
        MyScreen()
    }
}