package com.cheeke.surfy.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.favorite.navigation.FavoriteNavKey
import com.cheeke.surfy.feature.home.R
import com.cheeke.surfy.home.navigation.HomeNavKey

/**
 * 앱 최상단 내비게이션 바
 */
data class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @param:StringRes val titleTextId: Int,
    val type: Class<out NavKey>
)

val HOME = TopLevelDestination(
    selectedIcon = Icons.Rounded.Home,
    unselectedIcon = Icons.Outlined.Home,
    titleTextId = R.string.feature_home_name,
    type = HomeNavKey::class.java
)

val FAVORITE = TopLevelDestination(
    selectedIcon = Icons.Rounded.Favorite,
    unselectedIcon = Icons.Outlined.FavoriteBorder,
    titleTextId = com.cheeke.surfy.feature.favorite.R.string.feature_favorite_name,
    type = FavoriteNavKey::class.java
)

//val MY = TopLevelDestination(
//    selectedIcon = Icons.Rounded.Settings,
//    unselectedIcon = Icons.Outlined.Settings,
//    titleTextId = com.cheeke.surfy.feature.my.R.string.feature_my_name,
//    type = SettingNavKey::class.java
//)

val TOP_LEVEL_NAV_ITEMS = mapOf(
    HomeNavKey to HOME,
    FavoriteNavKey(tab = 0) to FAVORITE,
//    SettingNavKey to MY
)