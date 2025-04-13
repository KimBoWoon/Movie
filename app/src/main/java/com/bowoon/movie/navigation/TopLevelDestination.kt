package com.bowoon.movie.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.bowoon.favorite.navigation.Favorite
import com.bowoon.home.navigation.Home
import com.bowoon.my.navigation.My
import com.bowoon.search.navigation.Search
import com.slack.circuit.runtime.screen.Screen

/**
 * 앱 최상단 내비게이션 바
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val screen: Screen
) {
    HOME(
        selectedIcon = Icons.Rounded.Home,
        unselectedIcon = Icons.Outlined.Home,
        iconTextId = com.bowoon.movie.feature.home.R.string.feature_home_name,
        titleTextId = com.bowoon.movie.feature.home.R.string.feature_home_name,
        screen = Home
    ),
    SEARCH(
        selectedIcon = Icons.Rounded.Search,
        unselectedIcon = Icons.Outlined.Search,
        iconTextId = com.bowoon.movie.feature.search.R.string.feature_search_name,
        titleTextId = com.bowoon.movie.feature.search.R.string.feature_search_name,
        screen = Search
    ),
    FAVORITE(
        selectedIcon = Icons.Rounded.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        iconTextId = com.bowoon.movie.feature.favorite.R.string.feature_favorite_name,
        titleTextId = com.bowoon.movie.feature.favorite.R.string.feature_favorite_name,
        screen = Favorite
    ),
    MY(
        selectedIcon = Icons.Rounded.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        iconTextId = com.bowoon.movie.feature.my.R.string.feature_my_name,
        titleTextId = com.bowoon.movie.feature.my.R.string.feature_my_name,
        screen = My
    )
}