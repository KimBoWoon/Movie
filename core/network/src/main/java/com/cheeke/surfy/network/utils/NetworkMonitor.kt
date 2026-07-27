package com.cheeke.surfy.network.utils

import kotlinx.coroutines.flow.Flow

/**
 * Utility for reporting app connectivity status
 * 네트워크 상태를 알기 위한 인터페이스
 */
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}
