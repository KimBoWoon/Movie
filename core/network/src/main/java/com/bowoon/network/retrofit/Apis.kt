package com.bowoon.network.retrofit

import com.bowoon.network.TMDBApis
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * 데이터를 가져오는 Api
 * @param retrofit 레트로핏 모듈
 */
class Apis @Inject constructor(
    private val tmdbRetrofit: Retrofit
) {
    val tmdbApis = tmdbRetrofit.create(TMDBApis::class.java)
}