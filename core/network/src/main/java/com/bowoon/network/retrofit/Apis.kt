package com.bowoon.network.retrofit

import com.bowoon.network.KMDBApis
import com.bowoon.network.KOBISApis
import com.bowoon.network.di.KMDBRetrofit
import com.bowoon.network.di.KOBISRetrofit
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * 데이터를 가져오는 Api
 * @param retrofit 레트로핏 모듈
 */
@ViewModelScoped
class Apis @Inject constructor(
    @KMDBRetrofit private val kmdbRetrofit: Retrofit,
    @KOBISRetrofit private val kobisRetrofit: Retrofit
) {
    val kmdbApis = kmdbRetrofit.create(KMDBApis::class.java)
    val kobisApis = kobisRetrofit.create(KOBISApis::class.java)
}