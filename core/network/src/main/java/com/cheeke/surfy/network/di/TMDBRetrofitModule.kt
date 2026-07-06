package com.cheeke.surfy.network.di

import com.cheeke.surfy.network.MovieApis
import com.cheeke.surfy.network.PeopleApis
import com.cheeke.surfy.network.RxApiResultCallAdapterFactory
import com.cheeke.surfy.network.SearchApis
import com.cheeke.surfy.network.SeriesApis
import com.cheeke.surfy.network.SettingApis
import com.cheeke.surfy.network.SyncApis
import com.cheeke.surfy.network.TrendingApis
import com.cheeke.surfy.network.TvApis
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
object TMDBRetrofitModule {
    @Provides
    fun provideRetrofit(
        tmdbUrl: String,
//        customCallAdapter: CustomCallAdapter,
        serialization: Json,
        jsonMediaType: MediaType,
        client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(tmdbUrl)
//        .addCallAdapterFactory(customCallAdapter)
        .addCallAdapterFactory(RxApiResultCallAdapterFactory())
        .addConverterFactory(serialization.asConverterFactory(jsonMediaType))
        .client(client)
        .build()

    @Provides
    fun provideSettingApis(retrofit: Retrofit): SettingApis = retrofit.create(SettingApis::class.java)

    @Provides
    fun provideSearchApis(retrofit: Retrofit): SearchApis = retrofit.create(SearchApis::class.java)

    @Provides
    fun provideMovieApis(retrofit: Retrofit): MovieApis = retrofit.create(MovieApis::class.java)

    @Provides
    fun provideTvApis(retrofit: Retrofit): TvApis = retrofit.create(TvApis::class.java)

    @Provides
    fun providePeopleApis(retrofit: Retrofit): PeopleApis = retrofit.create(PeopleApis::class.java)

    @Provides
    fun provideSeriesApis(retrofit: Retrofit): SeriesApis = retrofit.create(SeriesApis::class.java)

    @Provides
    fun provideSyncApis(retrofit: Retrofit): SyncApis = retrofit.create(SyncApis::class.java)

    @Provides
    fun provideTrendingApis(retrofit: Retrofit): TrendingApis = retrofit.create(TrendingApis::class.java)
}