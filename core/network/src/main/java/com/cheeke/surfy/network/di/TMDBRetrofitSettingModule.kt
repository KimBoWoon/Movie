package com.cheeke.surfy.network.di

import com.cheeke.surfy.core.network.BuildConfig
import com.cheeke.surfy.network.utils.NetworkLogInterceptor
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
object TMDBRetrofitSettingModule {
    @Provides
    fun provideTMDBOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        okHttpProfilerInterceptor: OkHttpProfilerInterceptor,
        networkLogInterceptor: NetworkLogInterceptor
    ): OkHttpClient = OkHttpClient().newBuilder().apply {
        connectTimeout(timeout = 1, unit = TimeUnit.MINUTES)
        readTimeout(timeout = 30, unit = TimeUnit.SECONDS)
        writeTimeout(timeout = 15, unit = TimeUnit.SECONDS)
        addNetworkInterceptor(httpLoggingInterceptor)
        if (BuildConfig.IS_DEBUGGING_LOGGING) {
            addInterceptor(okHttpProfilerInterceptor)
            addInterceptor(networkLogInterceptor)
        }
        addInterceptor { chain: Interceptor.Chain ->
            chain.proceed(
                request = chain.request().newBuilder().apply {
                    addHeader(name = "accept", value = "application/json")
                    addHeader(name = "Authorization", value = "Bearer ${BuildConfig.TMDB_OPEN_API_KEY}")
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
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    @Provides
    fun provideOkHttpProfilerInterceptor(): OkHttpProfilerInterceptor = OkHttpProfilerInterceptor()

    @Provides
    fun provideTMDBUrl(): String = "https://api.themoviedb.org/"
}