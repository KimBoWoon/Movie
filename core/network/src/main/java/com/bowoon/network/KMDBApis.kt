package com.bowoon.network

import com.bowoon.network.model.KMDBMovieDetail
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * 서버에 데이터를 요청하는 서비스
 */
interface KMDBApis {
    @GET
    suspend fun getMovieInfo(
        @Url url: String
    ): ApiResponse<KMDBMovieDetail>
}