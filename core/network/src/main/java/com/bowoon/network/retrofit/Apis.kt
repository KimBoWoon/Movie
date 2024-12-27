package com.bowoon.network.retrofit

import com.bowoon.network.KOBISApis
import com.bowoon.network.TMDBApis
import com.bowoon.network.di.KOBISRetrofit
import com.bowoon.network.di.TMDBRetrofit
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * 데이터를 가져오는 Api
 * @param retrofit 레트로핏 모듈
 */
@ViewModelScoped
class Apis @Inject constructor(
    @KOBISRetrofit private val kobisRetrofit: Retrofit,
    @TMDBRetrofit private val tmdbRetrofit: Retrofit
) {
    val kobisApis = kobisRetrofit.create(KOBISApis::class.java)
    val tmdbApis = tmdbRetrofit.create(TMDBApis::class.java)
}