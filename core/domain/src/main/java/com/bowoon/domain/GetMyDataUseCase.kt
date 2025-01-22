package com.bowoon.domain

import com.bowoon.data.repository.MyDataRepository
import com.bowoon.model.MyData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyDataUseCase @Inject constructor(
    private val myDataRepository: MyDataRepository
) {
    operator fun invoke(): Flow<MyData?> = myDataRepository.myData
}