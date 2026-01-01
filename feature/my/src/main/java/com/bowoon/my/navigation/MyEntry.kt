package com.bowoon.my.navigation

import androidx.annotation.Keep
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.bowoon.my.MyScreen
import kotlinx.serialization.Serializable

@Serializable
@Keep
data object MyNavKey : NavKey

fun EntryProviderScope<NavKey>.myEntry(

) {
    entry<MyNavKey> {
        MyScreen()
    }
}