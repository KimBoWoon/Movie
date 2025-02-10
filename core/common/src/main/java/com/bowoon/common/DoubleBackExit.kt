package com.bowoon.common

import android.content.Context
import android.widget.Toast
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppDoubleBackToExit @AssistedInject constructor(
    @Assisted("context") private val context: Context,
    @Assisted("exitText") private val exitText: String
) {
    private val delayMillis = 1500L
    private var backPressedOnce = false

    @AssistedFactory
    interface AppDoubleBackToExitFactory {
        fun create(
            @Assisted("context") context: Context,
            @Assisted("exitText") exitText: String
        ): AppDoubleBackToExit
    }

    fun onBackPressed(callback: () -> Unit) {
        when (backPressedOnce) {
            true -> callback.invoke()
            false -> {
                Toast.makeText(context, exitText, Toast.LENGTH_SHORT).show()
                backPressedOnce = true
                CoroutineScope(Dispatchers.Main).launch {
                    delay(delayMillis)
                    backPressedOnce = false
                }
            }
        }
    }
}