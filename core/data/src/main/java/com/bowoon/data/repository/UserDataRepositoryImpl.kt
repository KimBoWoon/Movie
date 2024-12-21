package com.bowoon.data.repository

import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.DailyBoxOffice
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val datastore: InternalDataSource
) : UserDataRepository {
    override val userData: Flow<UserData> = datastore.userData

    override suspend fun updateDarkModeTheme(config: DarkThemeConfig) {
        datastore.updateDarkTheme(config)
    }

    override suspend fun updateBoxOfficeDate(date: String) {
        datastore.updateBoxOfficeDate(date)
    }

    override suspend fun updateDailyBoxOffices(dailyBoxOffices: List<DailyBoxOffice>) {
        datastore.updateDailyBoxOffices(dailyBoxOffices)
    }
}