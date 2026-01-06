package com.bowoon.movie.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.bowoon.favorite.navigation.FavoriteNavKey
import com.bowoon.home.navigation.HomeNavKey
import com.bowoon.my.navigation.MyNavKey

/**
 * 앱 최상단 내비게이션 바
 */
data class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @param:StringRes val titleTextId: Int
)

val HOME = TopLevelDestination(
    selectedIcon = Icons.Rounded.Home,
    unselectedIcon = Icons.Outlined.Home,
    titleTextId = com.bowoon.movie.feature.home.R.string.feature_home_name
)

val FAVORITE = TopLevelDestination(
    selectedIcon = Icons.Rounded.Favorite,
    unselectedIcon = Icons.Outlined.FavoriteBorder,
    titleTextId = com.bowoon.movie.feature.favorite.R.string.feature_favorite_name
)

val MY = TopLevelDestination(
    selectedIcon = Icons.Rounded.Settings,
    unselectedIcon = Icons.Outlined.Settings,
    titleTextId = com.bowoon.movie.feature.my.R.string.feature_my_name
)

val TOP_LEVEL_NAV_ITEMS = mapOf(
    HomeNavKey to HOME,
    FavoriteNavKey to FAVORITE,
    MyNavKey to MY
)