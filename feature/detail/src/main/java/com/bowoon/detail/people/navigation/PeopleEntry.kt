package com.bowoon.detail.people.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.bowoon.detail.people.PeopleScreen
import com.bowoon.detail.people.PeopleVM
import com.bowoon.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class PeopleNavKey(
    val id: Int
) : NavKey

fun EntryProviderScope<NavKey>.peopleEntry(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    entry<PeopleNavKey> { peopleRoute ->
        PeopleScreen(
            goToBack = goToBack,
            goToMovie = goToMovie,
            onShowSnackbar = onShowSnackbar,
            viewModel = hiltViewModel<PeopleVM, PeopleVM.Factory>(
                key = peopleRoute.id.toString(),
            ) { factory ->
                factory.create(id = peopleRoute.id)
            }
        )
    }
}

fun Navigator.navigateToPeople(
    id: Int
) {
    navigate(route = PeopleNavKey(id = id))
}