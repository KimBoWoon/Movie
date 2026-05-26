package com.cheeke.surfy.common

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerTest {
    /**
     * TestCoroutineScheduler와 연동된 FakeClock
     * advanceTimeBy()를 호출하면 clock.now()도 같이 움직임
     */
    private class FakeClock(
        private val scheduler: TestCoroutineScheduler
    ) : Clock {
        override fun now() = scheduler.currentTime
    }

    private fun buildTimer(
        scheduler: TestCoroutineScheduler,
        durationMillis: Long = 10_000L,
        tickMillis: Long = 1_000L,
        intervalMillis: Long? = null
    ): Timer {
        val dispatcher = StandardTestDispatcher(scheduler)
        return Timer(
            clock = FakeClock(scheduler),
            durationMillis = durationMillis,
            tickMillis = tickMillis,
            intervalMillis = intervalMillis,
            dispatcher = dispatcher
        )
    }

    @Test
    fun `초기 상태는 Idle이다`() {
        val timer = buildTimer(scheduler = TestCoroutineScheduler())
        assertEquals(TimerState.Idle, timer.state.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `durationMillis가 0이면 예외가 발생한다`() {
        buildTimer(scheduler = TestCoroutineScheduler(), durationMillis = 0L)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `tickMillis가 0이면 예외가 발생한다`() {
        buildTimer(scheduler = TestCoroutineScheduler(), tickMillis = 0L)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `intervalMillis가 0이면 예외가 발생한다`() {
        buildTimer(scheduler = TestCoroutineScheduler(), intervalMillis = 0L)
    }

    @Test
    fun `start 후 Running 상태가 된다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(1_000)
            runCurrent()
            assertTrue(timer.state.value is TimerState.Running)
        }
    }

    @Test
    fun `start를 두 번 호출해도 중복 실행되지 않는다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 10_000L)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(2_000)
            runCurrent()

            timer.start() // 무시되어야 함
            advanceTimeBy(1_000)
            runCurrent()

            val state = timer.state.value as TimerState.Running
            assertEquals(3_000L, state.elapsedMillis)
        }
    }

    @Test
    fun `Finished 상태에서 start를 호출하면 무시된다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 3_000L)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(3_001)
            runCurrent()
            assertEquals(TimerState.Finished, timer.state.value)

            timer.start() // 무시되어야 함
            advanceTimeBy(1_000)
            runCurrent()
            assertEquals(TimerState.Finished, timer.state.value)
        }
    }

    @Test
    fun `elapsed와 remain의 합은 duration과 같다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val duration = 10_000L
        val timer = buildTimer(scheduler = scheduler, durationMillis = duration)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(4_000)
            runCurrent()

            val state = timer.state.value as TimerState.Running
            assertEquals(duration, state.elapsedMillis + state.remainMillis)
        }
    }

    @Test
    fun `progress는 0f에서 1f 사이다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 10_000L)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(5_000)
            runCurrent()

            val state = timer.state.value as TimerState.Running
            assertTrue(state.progress in 0f..1f)
            assertEquals(0.5f, state.progress, 0.01f)
        }
    }

    @Test
    fun `duration이 지나면 Finished 상태가 된다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 5_000L)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(5_001)
            runCurrent()
            assertEquals(TimerState.Finished, timer.state.value)
        }
    }

    @Test
    fun `pause 후 Paused 상태가 된다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 10_000L)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(3_000)
            runCurrent()
            timer.pause()
            assertTrue(timer.state.value is TimerState.Paused)
        }
    }

    @Test
    fun `pause 후 elapsed가 유지된다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 10_000L)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(3_000)
            runCurrent()
            timer.pause()

            val state = timer.state.value as TimerState.Paused
            assertEquals(3_000L, state.elapsedMillis)
        }
    }

    @Test
    fun `pause 이후 시간이 지나도 elapsed가 증가하지 않는다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 10_000L)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(3_000)
            runCurrent()
            timer.pause()

            // pause 후 시간이 더 지나도
            advanceTimeBy(3_000)
            runCurrent()

            val state = timer.state.value as TimerState.Paused
            assertEquals(3_000L, state.elapsedMillis)
        }
    }

    @Test
    fun `resume 후 멈춘 시점부터 이어서 진행된다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 10_000L)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(3_000)
            runCurrent()
            timer.pause()

            timer.resume()
            advanceTimeBy(2_000)
            runCurrent()

            val state = timer.state.value as TimerState.Running
            // pause 시점 3_000 + resume 후 2_000 = 5_000
            assertEquals(5_000L, state.elapsedMillis)
        }
    }

    @Test
    fun `Running 상태에서 resume을 호출하면 무시된다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 10_000L)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(2_000)
            runCurrent()

            timer.resume() // 무시되어야 함
            advanceTimeBy(1_000)
            runCurrent()

            val state = timer.state.value as TimerState.Running
            assertEquals(3_000L, state.elapsedMillis)
        }
    }

    @Test
    fun `Idle 상태에서 pause를 호출하면 무시된다`() {
        val timer = buildTimer(scheduler = TestCoroutineScheduler())
        timer.pause()
        assertEquals(TimerState.Idle, timer.state.value)
    }

    @Test
    fun `stop 후 Idle 상태가 된다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 10_000L)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(3_000)
            runCurrent()
            timer.stop()
            assertEquals(TimerState.Idle, timer.state.value)
        }
    }

    @Test
    fun `stop 이후 시간이 지나도 상태가 변하지 않는다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 10_000L)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(3_000)
            runCurrent()
            timer.stop()

            advanceTimeBy(5_000)
            runCurrent()

            assertEquals(TimerState.Idle, timer.state.value)
        }
    }

    @Test
    fun `reset 후 Idle 상태가 되고 start 재호출이 가능하다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 3_000L)

        runTest(dispatcher) {
            timer.start()
            advanceTimeBy(3_001)
            runCurrent()
            assertEquals(TimerState.Finished, timer.state.value)

            timer.reset()
            assertEquals(TimerState.Idle, timer.state.value)

            timer.start()
            advanceTimeBy(1_000)
            runCurrent()
            assertTrue(timer.state.value is TimerState.Running)
        }
    }

    @Test
    fun `release 이후 start를 호출해도 상태가 변하지 않는다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(scheduler = scheduler, durationMillis = 10_000L)

        runTest(dispatcher) {
            timer.release()

            timer.start()
            advanceTimeBy(3_000)
            runCurrent()

            // scope가 cancel됐으므로 Idle 유지
            assertEquals(TimerState.Idle, timer.state.value)
        }
    }

    @Test
    fun `intervalMillis마다 IntervalEvent가 방출된다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(
            scheduler = scheduler,
            durationMillis = 15_000L,
            intervalMillis = 5_000L
        )

        runTest(dispatcher) {
            val events = mutableListOf<IntervalEvent>()
            val job = launch(start = CoroutineStart.UNDISPATCHED) {
                timer.intervalEvent.collect { events.add(it) }
            }

            timer.start()
            advanceTimeBy(15_001)
            runCurrent()
            job.cancel()

            assertEquals(3, events.size)
            assertEquals(1L, events[0].intervalCount)
            assertEquals(2L, events[1].intervalCount)
            assertEquals(3L, events[2].intervalCount)
        }
    }

    @Test
    fun `drift 발생 시 놓친 interval을 모두 방출한다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(
            scheduler = scheduler,
            durationMillis = 30_000L,
            intervalMillis = 5_000L
        )

        runTest(dispatcher) {
            val events = mutableListOf<IntervalEvent>()
            val job = launch(start = CoroutineStart.UNDISPATCHED) {
                timer.intervalEvent.collect { events.add(it) }
            }

            timer.start()
            // 15초를 한 번에 점프 → interval 3개를 한 번에 통과
            advanceTimeBy(15_001)
            runCurrent()
            job.cancel()

            assertEquals(3, events.size)
            assertEquals(5_000L, events[0].elapsedMillis)
            assertEquals(10_000L, events[1].elapsedMillis)
            assertEquals(15_000L, events[2].elapsedMillis)
        }
    }

    @Test
    fun `pause 후 resume 시 interval이 이어서 카운트된다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(
            scheduler = scheduler,
            durationMillis = 15_000L,
            intervalMillis = 5_000L
        )

        runTest(dispatcher) {
            val events = mutableListOf<IntervalEvent>()
            val job = launch(start = CoroutineStart.UNDISPATCHED) {
                timer.intervalEvent.collect { events.add(it) }
            }

            timer.start()
            advanceTimeBy(5_001)  // interval 1 발생
            runCurrent()
            timer.pause()

            timer.resume()
            advanceTimeBy(5_000)  // interval 2 발생
            runCurrent()
            job.cancel()

            assertTrue(events.size >= 2)
            assertEquals(1L, events[0].intervalCount)
            assertEquals(2L, events[1].intervalCount)
        }
    }

    @Test
    fun `stop 이후 intervalEvent가 방출되지 않는다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(
            scheduler = scheduler,
            durationMillis = 15_000L,
            intervalMillis = 5_000L
        )

        runTest(dispatcher) {
            val events = mutableListOf<IntervalEvent>()
            val job = launch(start = CoroutineStart.UNDISPATCHED) {
                timer.intervalEvent.collect { events.add(it) }
            }

            timer.start()
            advanceTimeBy(5_001)  // interval 1 발생
            runCurrent()
            timer.stop()

            // stop 이후 시간이 더 지나도
            advanceTimeBy(10_000)
            runCurrent()
            job.cancel()

            assertEquals(1, events.size)
        }
    }

    @Test
    fun `intervalMillis 없으면 IntervalEvent가 방출되지 않는다`() {
        val scheduler = TestCoroutineScheduler()
        val dispatcher = StandardTestDispatcher(scheduler)
        val timer = buildTimer(
            scheduler = scheduler,
            durationMillis = 10_000L,
            intervalMillis = null
        )

        runTest(dispatcher) {
            val events = mutableListOf<IntervalEvent>()
            val job = launch(start = CoroutineStart.UNDISPATCHED) {
                timer.intervalEvent.collect { events.add(it) }
            }

            timer.start()
            advanceTimeBy(10_001)
            runCurrent()
            job.cancel()

            assertTrue(events.isEmpty())
        }
    }
}