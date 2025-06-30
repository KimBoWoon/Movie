package com.bowoon.domain

import androidx.paging.testing.asSnapshot
import com.bowoon.testing.model.testRecommendedKeyword
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetRecommendKeywordUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var getRecommendKeywordUseCase: GetRecommendKeywordUseCase
    private lateinit var testPagingRepository: TestPagingRepository

    @Before
    fun setup() {
        testPagingRepository = TestPagingRepository()
        getRecommendKeywordUseCase = GetRecommendKeywordUseCase(
            pagingRepository = testPagingRepository
        )
    }

    @Test
    fun getRecommendKeyword() = runTest {
        val recommendKeyword = getRecommendKeywordUseCase()

        assertEquals(
            recommendKeyword.asSnapshot(),
            testRecommendedKeyword
        )
    }
}