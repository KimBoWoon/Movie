package com.cheeke.surfy.favorite

import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.multibindings.IntoMap

@MapKey
annotation class FavoriteTabKey(val value: String)

@Module
@InstallIn(value = [ViewModelComponent::class])
abstract class FavoriteTabModule {
    @Binds
    @IntoMap
    @FavoriteTabKey(value = FavoriteKeys.MOVIE)
    abstract fun bindMovieTab(movieTab: MovieTab): FavoriteTab

    @Binds
    @IntoMap
    @FavoriteTabKey(value = FavoriteKeys.TV)
    abstract fun bindTvTab(tvTab: TvTab): FavoriteTab

    @Binds
    @IntoMap
    @FavoriteTabKey(value = FavoriteKeys.PEOPLE)
    abstract fun bindPeopleTab(peopleTab: PeopleTab): FavoriteTab
}