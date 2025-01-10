package com.bowoon.domain

import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MyData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class GetMyDataUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(): Flow<MyData> = userDataRepository.userData.mapNotNull { it.myData }
}