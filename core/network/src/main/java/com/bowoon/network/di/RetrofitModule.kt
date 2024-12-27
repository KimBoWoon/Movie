package com.bowoon.network.di

import com.bowoon.network.CustomCallAdapter
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    @KOBISRetrofit
    fun provideKobisRetrofit(
        @OtherOkHttp client: OkHttpClient,
        customCallAdapter: CustomCallAdapter,
        serialization: Json,
        jsonMediaType: MediaType
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://www.kobis.or.kr/")
        .addCallAdapterFactory(customCallAdapter)
        .addConverterFactory(serialization.asConverterFactory(jsonMediaType))
        .client(client)
        .build()

    @Provides
    @TMDBRetrofit
    fun provideTmdbRetrofit(
        @TMDBOkHttp client: OkHttpClient,
        customCallAdapter: CustomCallAdapter,
        serialization: Json,
        jsonMediaType: MediaType
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/")
        .addCallAdapterFactory(customCallAdapter)
        .addConverterFactory(serialization.asConverterFactory(jsonMediaType))
        .client(client)
        .build()
}