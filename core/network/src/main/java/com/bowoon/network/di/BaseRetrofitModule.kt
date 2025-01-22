package com.bowoon.network.di

import com.bowoon.network.BuildConfig
import com.bowoon.network.NetworkLogInterceptor
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object BaseRetrofitModule {
    @Provides
    fun provideTMDBOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        okHttpProfilerInterceptor: OkHttpProfilerInterceptor,
        networkLogInterceptor: NetworkLogInterceptor
    ): OkHttpClient = OkHttpClient().newBuilder().apply {
        connectTimeout(1, TimeUnit.MINUTES)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(15, TimeUnit.SECONDS)
        addNetworkInterceptor(httpLoggingInterceptor)
        if (BuildConfig.IS_DEBUGGING_LOGGING) {
            addInterceptor(okHttpProfilerInterceptor)
            addInterceptor(networkLogInterceptor)
        }
        addInterceptor { chain: Interceptor.Chain ->
            chain.proceed(
                chain.request().newBuilder().apply {
                    addHeader("accept", "application/json")
                    addHeader("Authorization", "Bearer ${BuildConfig.TMDB_OPEN_API_KEY}")
                }.build()
            )
        }
    }.build()

    @Provides
    fun provideKotlinSerialization(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Provides
    fun provideJsonMediaType(): MediaType = "application/json".toMediaType()

    @Provides
    fun provideInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    fun provideOkHttpProfilerInterceptor(): OkHttpProfilerInterceptor = OkHttpProfilerInterceptor()
}