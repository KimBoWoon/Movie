package com.bowoon.data.repository

import com.bowoon.model.MyData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MyDataRepository {
    val myData: StateFlow<MyData>
    val posterUrl: Flow<String>
    suspend fun syncWith(): Boolean
}