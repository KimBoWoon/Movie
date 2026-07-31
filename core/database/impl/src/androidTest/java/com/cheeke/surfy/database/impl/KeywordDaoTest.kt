package com.cheeke.surfy.database.impl

import androidx.paging.PagingSource
import com.cheeke.surfy.database.impl.model.KeywordEntity
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

internal class KeywordDaoTest : DatabaseTest() {
    val keyword1 = KeywordEntity(
        id = 1,
        keyword = "keyword_1",
        timestamp = 1234
    )
    val keyword2 = KeywordEntity(
        id = 2,
        keyword = "keyword_2",
        timestamp = 1235
    )

    @Test
    fun getKeywords() = runTest {
        keywordDao.insertOrReplaceKeyword(entity = keyword1)
        keywordDao.insertOrReplaceKeyword(entity = keyword2)

        val paging = keywordDao.getKeywords().load(
            params = PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(
            expected = paging.data,
            actual = listOf(keyword2, keyword1)
        )
    }

    @Test
    fun insertOrReplaceKeyword() = runTest {
        val beforeInsert = keywordDao.getKeywords().load(
            params = PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(
            expected = beforeInsert.data,
            actual = emptyList()
        )

        keywordDao.insertOrReplaceKeyword(entity = keyword1)

        val afterInsert = keywordDao.getKeywords().load(
            params = PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(
            expected = afterInsert.data,
            actual = listOf(keyword1)
        )
    }

    @Test
    fun deleteAll() = runTest {
        keywordDao.insertOrReplaceKeyword(entity = keyword1)
        keywordDao.insertOrReplaceKeyword(entity = keyword2)

        val pagingBeforeDelete = keywordDao.getKeywords().load(
            params = PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(
            expected = pagingBeforeDelete.data,
            actual = listOf(keyword2, keyword1)
        )

        keywordDao.deleteAll()

        val pagingAfterDelete = keywordDao.getKeywords().load(
            params = PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(
            expected = pagingAfterDelete.data,
            actual = emptyList()
        )
    }

    @Test
    fun delete() = runTest {
        keywordDao.insertOrReplaceKeyword(entity = keyword1)

        val pagingBeforeDelete = keywordDao.getKeywords().load(
            params = PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(
            expected = pagingBeforeDelete.data,
            actual = listOf(keyword1)
        )

        keywordDao.delete(id = keyword1.id)

        val pagingAfterDelete = keywordDao.getKeywords().load(
            params = PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(
            expected = pagingAfterDelete.data,
            actual = emptyList()
        )
    }
}