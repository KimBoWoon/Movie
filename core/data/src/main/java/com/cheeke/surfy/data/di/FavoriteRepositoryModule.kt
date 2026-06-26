package com.cheeke.surfy.data.di

import com.cheeke.surfy.data.repository.FavoriteKeys
import com.cheeke.surfy.data.repository.FavoriteMovieRepository
import com.cheeke.surfy.data.repository.FavoritePeopleRepository
import com.cheeke.surfy.data.repository.FavoriteRepository
import com.cheeke.surfy.data.repository.FavoriteTvRepository
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.multibindings.IntoMap

@MapKey
annotation class FavoriteTabKey(val value: FavoriteKeys)

@Module
@InstallIn(value = [ViewModelComponent::class])
abstract class FavoriteRepositoryModule {
    @Binds
    @IntoMap
    @FavoriteTabKey(value = FavoriteKeys.MOVIE)
    abstract fun bindMovieTab(movieTab: FavoriteMovieRepository): FavoriteRepository

    @Binds
    @IntoMap
    @FavoriteTabKey(value = FavoriteKeys.TV)
    abstract fun bindTvTab(tvTab: FavoriteTvRepository): FavoriteRepository

    @Binds
    @IntoMap
    @FavoriteTabKey(value = FavoriteKeys.PEOPLE)
    abstract fun bindPeopleTab(peopleTab: FavoritePeopleRepository): FavoriteRepository
}