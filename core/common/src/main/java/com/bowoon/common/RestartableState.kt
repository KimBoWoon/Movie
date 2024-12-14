package com.bowoon.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn

sealed interface StartedRestartableCommandProvider {
    fun commands(): List<SharingCommand>

    data object StopAndReset : StartedRestartableCommandProvider {
        override fun commands() = listOf(
            SharingCommand.STOP_AND_RESET_REPLAY_CACHE,
            SharingCommand.START
        )
    }

    data object Stop : StartedRestartableCommandProvider {
        override fun commands() = listOf(
            SharingCommand.STOP,
            SharingCommand.START
        )
    }
}

interface StartedRestartable : SharingStarted {
    fun restart()
}

// 구현체
private class StartedRestartableImpl(
    private val sharingStarted: SharingStarted,
    private val provider: StartedRestartableCommandProvider
) : StartedRestartable {
    private val flow = MutableSharedFlow<SharingCommand>(extraBufferCapacity = 2)

    override fun restart() {
        provider.commands().forEach {
            flow.tryEmit(it)
        }
    }

    override fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> {
        return merge(flow, sharingStarted.command(subscriptionCount))
    }
}

interface RestartableStateFlow<out T> : StateFlow<T> {
    fun restart()
}

// 구현체
private class RestartableStateFlowImpl<T>(
    flow: StateFlow<T>,
    private val started: StartedRestartable,
) : RestartableStateFlow<T>, StateFlow<T> by flow {
    override fun restart() {
        started.restart()
    }
}

fun <T> Flow<T>.restartableStateIn(
    scope: CoroutineScope,
    started: SharingStarted,
    initialValue: T,
    restartableCommandProvider: StartedRestartableCommandProvider = StartedRestartableCommandProvider.Stop
): RestartableStateFlow<T> {
    val sharingStarted = StartedRestartableImpl(started, restartableCommandProvider)
    val state = stateIn(scope, sharingStarted, initialValue)

    return RestartableStateFlowImpl(state, sharingStarted)
}