package com.cheeke.surfy.common

import android.os.SystemClock
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * Timer의 현재 상태
 */
sealed interface TimerState {
    /**
     * 아직 시작하지 않은 상태
     *
     * 예:
     * - Timer 생성 직후
     * - stop()
     * - reset()
     */
    data object Idle : TimerState

    /**
     * Timer가 실행 중인 상태
     *
     * UI에서는 보통:
     * - 진행률(progress bar)
     * - 남은 시간 표시
     * - 경과 시간 표시
     *
     * 등에 사용
     */
    data class Running(
        /**
         * 지금까지 진행된 시간(ms)
         *
         * 예:
         * duration = 10_000
         * 현재 3초 진행
         *
         * elapsedMillis = 3_000
         */
        val elapsedMillis: Long,

        /**
         * 남은 시간(ms)
         *
         * 예:
         * duration = 10_000
         * 현재 3초 진행
         *
         * remainMillis = 7_000
         */
        val remainMillis: Long,

        /**
         * 진행률 (0f ~ 1f)
         *
         * 예:
         * 0f   -> 시작 직후
         * 0.5f -> 50% 진행
         * 1f   -> 완료
         *
         * ProgressBar 등에 사용하기 좋음
         */
        val progress: Float
    ) : TimerState

    /**
     * Timer가 일시정지된 상태
     *
     * Running 상태의 값을 그대로 유지함
     *
     * resume() 시 이 값을 기반으로 이어서 진행
     */
    data class Paused(
        /**
         * pause() 시점까지 진행된 시간(ms)
         *
         * resume() 시 시작 기준 계산에 사용됨
         */
        val elapsedMillis: Long,

        /**
         * pause() 시점 기준 남은 시간(ms)
         *
         * UI에 남은 시간 표시 등에 사용
         */
        val remainMillis: Long,

        /**
         * pause() 시점 기준 진행률
         *
         * pause된 상태에서도
         * progress UI를 유지하기 위해 사용
         */
        val progress: Float
    ) : TimerState

    /**
     * Timer가 정상적으로 종료된 상태
     *
     * durationMillis까지 모두 진행 완료된 상태
     *
     * 이후 다시 실행하려면:
     * reset() -> start()
     *
     * 순서 필요
     */
    data object Finished : TimerState
}

/**
 * 특정 interval마다 발생하는 이벤트
 *
 * 예:
 * intervalMillis = 5_000
 *
 * -> 5초마다 이벤트 발생
 *
 * 사용 예:
 * - 광고 갱신
 * - heartbeat
 * - analytics
 * - autosave
 * - 특정 주기 작업
 */
data class IntervalEvent(
    /**
     * 몇 번째 interval 이벤트인지
     *
     * 예:
     * 1 -> 첫 번째 5초
     * 2 -> 두 번째 10초
     * 3 -> 세 번째 15초
     */
    val intervalCount: Long,

    /**
     * 이벤트가 발생한 누적 경과 시간(ms)
     *
     * 예:
     * intervalMillis = 5_000
     *
     * 첫 이벤트  -> 5_000
     * 두 번째    -> 10_000
     * 세 번째    -> 15_000
     *
     * drift 보정 시에도
     * 실제 interval 기준 시간을 유지하기 위해 사용
     */
    val elapsedMillis: Long
)

interface Clock {
    fun now(): Long
}

class ClockWrapper : Clock {
    override fun now(): Long {
        return SystemClock.elapsedRealtime()
    }
}

@ActivityRetainedScoped
class Timer(
    private val clock: Clock = ClockWrapper(),
    private val durationMillis: Long,
    /** UI 갱신 등에 사용하는 기본 tick 주기 */
    private val tickMillis: Long = 1_000L,
    /**
     * 특정 주기 이벤트
     * 예: 5_000L → 5초마다 IntervalEvent 방출
     */
    private val intervalMillis: Long? = null,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    init {
        require(value = durationMillis > 0L) { "durationMillis must be > 0" }
        require(value = tickMillis > 0L) { "tickMillis must be > 0" }
        intervalMillis?.let { require(it > 0L) { "intervalMillis must be > 0" } }
    }

    private val scope = CoroutineScope(context = SupervisorJob() + dispatcher)
    private var timerJob: Job? = null
    private val _state = MutableStateFlow<TimerState>(value = TimerState.Idle)
    val state: StateFlow<TimerState> = _state.asStateFlow()

    /**
     * interval 이벤트는 state와 분리
     *
     * 이유:
     * - 이벤트는 일회성
     * - 상태와 의미가 다름
     * - 중간 interval 유실 방지
     *
     * replay = 0: 구독 이전 이벤트는 의도적으로 무시
     */
    private val _intervalEvent = MutableSharedFlow<IntervalEvent>(replay = 0)
    val intervalEvent: SharedFlow<IntervalEvent> = _intervalEvent.asSharedFlow()

    fun start() {
        if (timerJob?.isActive == true) return

        // Finished 상태에서는 reset() 후 start() 하도록 강제
        if (_state.value is TimerState.Finished) return

        internalStart(initialElapsedMillis = 0L)
    }

    fun resume() {
        if (timerJob?.isActive == true) return

        val paused = _state.value as? TimerState.Paused ?: return

        internalStart(initialElapsedMillis = paused.elapsedMillis)
    }

    fun pause() {
        val running = _state.value as? TimerState.Running ?: return

        timerJob?.cancel()
        timerJob = null

        _state.value = TimerState.Paused(
            elapsedMillis = running.elapsedMillis,
            remainMillis = running.remainMillis,
            progress = running.progress
        )
    }

    /**
     * 진행 중단 후 Idle로 복귀
     * 이후 start() 재호출 가능
     */
    fun stop() {
        timerJob?.cancel()
        timerJob = null
        _state.value = TimerState.Idle
    }

    /**
     * stop()과 동일하나 명시적 reset 의도를 표현
     * Finished 상태에서도 Idle로 복귀
     */
    fun reset() {
        timerJob?.cancel()
        timerJob = null
        _state.value = TimerState.Idle
    }

    /** scope 전체 해제. 이후 재사용 불가 */
    fun release() {
        scope.cancel()
    }

    private fun internalStart(initialElapsedMillis: Long) {
        timerJob?.cancel()

        timerJob = scope.launch {
            val startedAt = clock.now() - initialElapsedMillis
            val endAt = startedAt + durationMillis
            // resume 시 interval 이어가기
            var intervalIndex = if (intervalMillis != null) {
                initialElapsedMillis / intervalMillis
            } else {
                0L
            }
            var nextIntervalAt = if (intervalMillis != null) {
                (intervalIndex + 1) * intervalMillis
            } else {
                Long.MAX_VALUE
            }

            while (isActive) {
                val now = clock.now()
                val remain = max(0L, endAt - now)
                val elapsed = durationMillis - remain
                val progress = elapsed.toFloat() / durationMillis.toFloat()

                // interval drift 복구: 놓친 interval 모두 방출
                intervalMillis?.let {
                    while (elapsed >= nextIntervalAt) {
                        intervalIndex++
                        _intervalEvent.emit(
                            value = IntervalEvent(
                                intervalCount = intervalIndex,
                                elapsedMillis = nextIntervalAt
                            )
                        )
                        nextIntervalAt += intervalMillis
                    }
                }

                _state.value = TimerState.Running(
                    elapsedMillis = elapsed,
                    remainMillis = remain,
                    progress = progress
                )

                if (remain <= 0L) break

                // drift 최소화: 다음 tick boundary까지만 delay
                val nextTickDelay = tickMillis - (elapsed % tickMillis)
                delay(timeMillis = nextTickDelay)
            }

            _state.value = TimerState.Finished
        }
    }
}