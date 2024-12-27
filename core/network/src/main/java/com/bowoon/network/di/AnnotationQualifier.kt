package com.bowoon.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KOBISRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TMDBRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TMDBOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OtherOkHttp