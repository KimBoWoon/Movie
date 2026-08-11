package com.cheeke.surfy.detail.impl

import com.cheeke.surfy.model.Media
import kotlinx.coroutines.flow.Flow

interface DetailRepository<T : Media> {
    fun getData(id: Int): Flow<T>
}