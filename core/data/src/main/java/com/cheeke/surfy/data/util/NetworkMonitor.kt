package com.cheeke.surfy.data.util

import io.reactivex.rxjava3.core.Flowable

/**
 * Utility for reporting app connectivity status
 * 네트워크 상태를 알기 위한 인터페이스
 */
interface NetworkMonitor {
    val isOnline: Flowable<Boolean>
}
