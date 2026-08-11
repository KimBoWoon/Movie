package com.cheeke.surfy.detail.impl.people

import com.cheeke.surfy.detail.impl.DetailRepository
import com.cheeke.surfy.model.CombineCredits
import com.cheeke.surfy.model.ExternalIds
import com.cheeke.surfy.model.People
import kotlinx.coroutines.flow.Flow

interface PeopleDetailRepository : DetailRepository<People> {
    override fun getData(id: Int): Flow<People>
    fun getCombineCredits(personId: Int): Flow<CombineCredits>
    fun getExternalIds(personId: Int): Flow<ExternalIds>
}