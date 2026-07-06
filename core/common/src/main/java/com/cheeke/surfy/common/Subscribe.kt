package com.cheeke.surfy.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

@Composable
fun <T : Any> Observable<T>.subscribeAsState(
    initial: T
): State<T> {
    val state = remember {
        mutableStateOf(value = initial)
    }

    DisposableEffect(key1 = this) {
        val disposable = subscribe(
            { state.value = it },
            { it.printStackTrace() }
        )

        onDispose { disposable.dispose() }
    }

    return state
}

@Composable
fun <T : Any> Flowable<T>.subscribeAsState(
    initial: T
): State<T> {
    val state = remember {
        mutableStateOf(value = initial)
    }

    DisposableEffect(key1 = this) {
        val disposable = subscribe(
            { state.value = it },
            { it.printStackTrace() }
        )

        onDispose { disposable.dispose() }
    }

    return state
}