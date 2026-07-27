package com.cheeke.surfy.favorite.impl

import com.cheeke.surfy.favorite.api.FavoriteContentType
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.multibindings.IntoMap

@MapKey
annotation class FavoriteTabKey(val value: FavoriteContentType)

@Module
@InstallIn(value = [ViewModelComponent::class])
abstract class FavoriteRepositoryModule {
    @Binds
    @IntoMap
    @FavoriteTabKey(value = FavoriteContentType.MOVIE)
    abstract fun bindMovieTab(movieTab: FavoriteMovieRepository): FavoriteRepository

    @Binds
    @IntoMap
    @FavoriteTabKey(value = FavoriteContentType.TV)
    abstract fun bindTvTab(tvTab: FavoriteTvRepository): FavoriteRepository

    @Binds
    @IntoMap
    @FavoriteTabKey(value = FavoriteContentType.PEOPLE)
    abstract fun bindPeopleTab(peopleTab: FavoritePeopleRepository): FavoriteRepository
}