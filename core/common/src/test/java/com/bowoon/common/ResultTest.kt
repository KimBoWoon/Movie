package com.bowoon.common

import app.cash.turbine.test
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ResultTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun resultTest() = runTest {
        flow {
            emit(1)
            throw Exception("Test Done")
        }
            .asResult()
            .test {
                assertEquals(Result.Loading, awaitItem())
                assertEquals(Result.Success(1), awaitItem())

                when (val errorResult = awaitItem()) {
                    is Result.Error -> assertEquals("Test Done", errorResult.throwable.message)
                    Result.Loading,
                    is Result.Success, -> throw IllegalStateException("The flow should have emitted an Error Result")
                }

                awaitComplete()
            }
    }
}