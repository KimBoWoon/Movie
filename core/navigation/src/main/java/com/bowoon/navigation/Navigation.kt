package com.bowoon.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer

/**
 * 앱내에 네비개이션의 상태를 생성하고 종료를 처리
 *
 * @param startRoute 앱의 시작 지점
 * @param topLevelRoutes 최상위 목적지점 집합
 */
@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(valueSerializer = NavKeySerializer())
    ) {
        mutableStateOf(value = startRoute)
    }
    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }

    return remember(key1 = startRoute, key2 = topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            backStacks = backStacks,
            topLevelRoute = topLevelRoute
        )
    }
}

/**
 * 네비개이션의 상태를 저장하는 클래스
 *
 * @param startRoute 앱의 첫 시작 지점
 * @param topLevelRoute 현재 네비개이션의 최상위 목적지
 * @param backStacks 각각의 최상위 목적지에 해당하는 네비개이션 백스택을 저장하는 맵
 */
class NavigationState(
    val startRoute: NavKey,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
    topLevelRoute: MutableState<NavKey>
) {
    var topLevelRoute: NavKey by topLevelRoute
    val stacksInUse: List<NavKey>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }
}

/**
 * Convert NavigationState into NavEntries.
 */
@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>()
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}

/**
 * 내비게이션의 상태를 업데이트하여 내비게이션의 이벤트 처리
 */
class Navigator(val state: NavigationState) {
    /**
     * 화면 이동
     *
     * @param route 목적지
     */
    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            // 현재 최상위 목적지를 변경
            state.topLevelRoute = route
        } else {
            // 최상위 목적지에 스택을 추가
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    /**
     * 뒤로 이동
     */
    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // 현재 목적지가 최상위 목적지와 같으면 시작지점으로 보냄
        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            // 아니면 뒤로 이동
            currentStack.removeLastOrNull()
        }
    }
}