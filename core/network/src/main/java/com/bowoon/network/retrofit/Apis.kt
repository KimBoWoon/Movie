package com.bowoon.network.retrofit

import com.bowoon.network.KMDBApis
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * 데이터를 가져오는 Api
 * @param retrofit 레트로핏 모듈
 */
@ViewModelScoped
class Apis @Inject constructor(
    private val retrofit: Retrofit
) {
    val kmdbApis = retrofit.create(KMDBApis::class.java)
}