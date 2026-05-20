package com.cheeke.surfy.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.cheeke.surfy.ui.root.RootTab
import com.slack.circuit.runtime.screen.Screen

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @param:StringRes val iconTextId: Int,
    @param:StringRes val titleTextId: Int,
    val screen: Screen,
    val rootTab: RootTab
) {
    HOME(
        selectedIcon = Icons.Rounded.Home,
        unselectedIcon = Icons.Outlined.Home,
        iconTextId = com.cheeke.surfy.feature.home.R.string.feature_home_name,
        titleTextId = com.cheeke.surfy.feature.home.R.string.feature_home_name,
        screen = HomeScreen,
        rootTab = RootTab.HOME
    ),
    FAVORITE(
        selectedIcon = Icons.Rounded.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        iconTextId = com.cheeke.surfy.feature.favorite.R.string.feature_favorite_name,
        titleTextId = com.cheeke.surfy.feature.favorite.R.string.feature_favorite_name,
        screen = FavoriteScreen(),
        rootTab = RootTab.FAVORITE
    )
}