package com.bowoon.movie.deeplink

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.bowoon.favorite.navigation.FavoriteNavKey
import com.bowoon.home.navigation.HomeNavKey
import com.bowoon.my.navigation.MyNavKey

//internal fun Uri?.toKey(): NavKey {
//    if (this == null) return HomeNavKey
//
//    val paths = pathSegments
//
//    if (pathSegments.isEmpty()) return HomeNavKey
//
//    return when(paths.first()) {
//        "home" -> HomeNavKey
//        "favorite" -> {
////            val firstName = pathSegments[1]
////            val location = pathSegments[2]
////            val user = LIST_USERS.find {
////                it.firstName == firstName && it.location == location
////            }
////            if (user == null) Users else UserDetail(user)
//            val tab = pathSegments[1]
//            val index = pathSegments[2]
//            FavoriteNavKey
//        }
//        "my" -> MyNavKey
//        else -> HomeNavKey
//    }
//}