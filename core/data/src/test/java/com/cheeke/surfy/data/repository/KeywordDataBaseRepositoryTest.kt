package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.data.testdouble.TestKeywordDao
import com.cheeke.surfy.database.model.KeywordEntity
import com.cheeke.surfy.testing.model.keywordList
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class KeywordDataBaseRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val keywordDao = TestKeywordDao()
    private val repository = KeywordDataBaseRepositoryImpl(keywordDao = keywordDao)
    private val keyword1 = KeywordEntity(id = 1, keyword = "title_1", timestamp = 1)
    private val keyword2 = KeywordEntity(id = 2, keyword = "title_2", timestamp = 2)

    @Test
    fun getKeywordsTest() = runTest {
        val getKeyword = repository.getKeywords().load(
            params = PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(
            expected = getKeyword.data,
            actual = keywordList
        )
    }

    @Test
    fun insertTest() = runTest {
        assertEquals(
            expected = keywordDao.entitiesStateFlow.value,
            actual = emptyList()
        )

//        repository.insert(keyword = keyword1.keyword)
        keywordDao.insertOrReplaceKeyword(entity = keyword1)

        assertEquals(
            expected = keywordDao.entitiesStateFlow.value,
            actual = listOf(keyword1)
        )

//        repository.insert(keyword = keyword2.keyword)
        keywordDao.insertOrReplaceKeyword(entity = keyword2)

        assertEquals(
            expected = keywordDao.entitiesStateFlow.value,
            actual = listOf(keyword1, keyword2)
        )
    }

    @Test
    fun deleteAll() = runTest {
//        repository.insert(keyword = keyword1.keyword)
//        repository.insert(keyword = keyword2.keyword)
        keywordDao.insertOrReplaceKeyword(entity = keyword1)
        keywordDao.insertOrReplaceKeyword(entity = keyword2)

        assertEquals(
            expected = keywordDao.entitiesStateFlow.value,
            actual = listOf(keyword1, keyword2)
        )

        keywordDao.deleteAll()

        assertEquals(
            expected = keywordDao.entitiesStateFlow.value,
            actual = emptyList()
        )
    }

    @Test
    fun delete() = runTest {
//        repository.insert(keyword = keyword1.keyword)
//        repository.insert(keyword = keyword2.keyword)
        keywordDao.insertOrReplaceKeyword(entity = keyword1)
        keywordDao.insertOrReplaceKeyword(entity = keyword2)

        assertEquals(
            expected = keywordDao.entitiesStateFlow.value,
            actual = listOf(keyword1, keyword2)
        )

        keywordDao.delete(id = keyword1.id)

        assertEquals(
            expected = keywordDao.entitiesStateFlow.value,
            actual = listOf(keyword2)
        )
    }
}