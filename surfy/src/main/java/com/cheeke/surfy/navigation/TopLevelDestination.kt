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
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
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
//    SEARCH(
//        selectedIcon = Icons.Rounded.Search,
//        unselectedIcon = Icons.Outlined.Search,
//        iconTextId = com.bowoon.movie.feature.search.R.string.feature_search_name,
//        titleTextId = com.bowoon.movie.feature.search.R.string.feature_search_name,
//        screen = Search
//    ),
    FAVORITE(
        selectedIcon = Icons.Rounded.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        iconTextId = com.cheeke.surfy.feature.favorite.R.string.feature_favorite_name,
        titleTextId = com.cheeke.surfy.feature.favorite.R.string.feature_favorite_name,
        screen = FavoriteScreen(),
        rootTab = RootTab.FAVORITE
    ),
//    MY(
//        selectedIcon = Icons.Rounded.Settings,
//        unselectedIcon = Icons.Outlined.Settings,
//        iconTextId = com.bowoon.movie.feature.my.R.string.feature_my_name,
//        titleTextId = com.bowoon.movie.feature.my.R.string.feature_my_name,
//        screen = My
//    )
}

/**
 * 앱 최상단 내비게이션 바
 */
//data class TopLevelDestination(
//    val selectedIcon: ImageVector,
//    val unselectedIcon: ImageVector,
//    @param:StringRes val titleTextId: Int,
//    val type: Class<out NavKey>
//)
//
//val HOME = TopLevelDestination(
//    selectedIcon = Icons.Rounded.Home,
//    unselectedIcon = Icons.Outlined.Home,
//    titleTextId = R.string.feature_home_name,
//    type = HomeNavKey::class.java
//)
//
//val FAVORITE = TopLevelDestination(
//    selectedIcon = Icons.Rounded.Favorite,
//    unselectedIcon = Icons.Outlined.FavoriteBorder,
//    titleTextId = com.cheeke.surfy.feature.favorite.R.string.feature_favorite_name,
//    type = FavoriteNavKey::class.java
//)
//
////val MY = TopLevelDestination(
////    selectedIcon = Icons.Rounded.Settings,
////    unselectedIcon = Icons.Outlined.Settings,
////    titleTextId = com.cheeke.surfy.feature.my.R.string.feature_my_name,
////    type = SettingNavKey::class.java
////)
//
//val TOP_LEVEL_NAV_ITEMS = mapOf(
//    HomeNavKey to HOME,
//    FavoriteNavKey(tab = 0) to FAVORITE,
////    SettingNavKey to MY
//)