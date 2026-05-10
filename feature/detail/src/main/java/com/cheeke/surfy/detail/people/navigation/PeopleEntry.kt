package com.cheeke.surfy.detail.people.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.detail.people.PeopleScreen
import com.cheeke.surfy.detail.people.PeopleVM
import kotlinx.serialization.Serializable

@Serializable
data class PeopleNavKey(
    val id: Int
) : NavKey

fun EntryProviderScope<NavKey>.peopleEntry(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    entry<PeopleNavKey> { peopleRoute ->
        PeopleScreen(
            goToBack = goToBack,
            goToMovie = goToMovie,
            goToTv = goToTv,
            onShowSnackbar = onShowSnackbar,
            viewModel = hiltViewModel<PeopleVM, PeopleVM.Factory>(
                key = peopleRoute.id.toString(),
            ) { factory ->
                factory.create(id = peopleRoute.id)
            }
        )
    }
}

fun NavBackStack<NavKey>.goToPeople(id: Int) = add(element = PeopleNavKey(id = id))