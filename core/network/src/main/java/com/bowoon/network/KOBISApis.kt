package com.bowoon.network

import com.bowoon.network.model.NetworkBoxOffice
import com.bowoon.network.model.NetworkMovieData
import retrofit2.http.GET
import retrofit2.http.Query

interface KOBISApis {
    @GET("/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json")
    suspend fun getDailyBoxOffice(
        @Query("key") key: String,
        @Query("targetDt") targetDt: String
    ): ApiResponse<NetworkBoxOffice>

    @GET("/kobisopenapi/webservice/rest/movie/searchMovieInfo.json")
    suspend fun getMovieInfo(
        @Query("key") key: String,
        @Query("movieCd") movieCd: String
    ): ApiResponse<NetworkMovieData>
}