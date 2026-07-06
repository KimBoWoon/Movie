package com.cheeke.surfy.utils

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.core.util.Consumer
import com.cheeke.surfy.common.isSystemInDarkTheme
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable

fun ComponentActivity.isSystemInDarkTheme(): Flowable<Boolean> =
    Flowable.create({ emitter ->
        emitter.onNext(resources.configuration.isSystemInDarkTheme)

        val listener = Consumer<Configuration> {
            emitter.onNext(it.isSystemInDarkTheme)
        }

        addOnConfigurationChangedListener(listener)

        emitter.setCancellable {
            removeOnConfigurationChangedListener(listener)
        }

    }, BackpressureStrategy.LATEST)
        .distinctUntilChanged()