package com.bowoon.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate

fun Modifier.animateRotation(
    expanded: Boolean = false,
    startAngle: Float,
    endAngle: Float,
    animateMillis: Int
) = composed(
    factory = {
        var currentRotation by remember { mutableFloatStateOf(0f) }
        val rotation = remember { Animatable(0f) }

        if (expanded) {
            LaunchedEffect(rotation) {
                rotation.animateTo(
                    targetValue = currentRotation + endAngle,
                    animationSpec = tween(durationMillis = animateMillis)
                ) { currentRotation = value }
            }
        } else {
            LaunchedEffect(rotation) {
                rotation.animateTo(
                    targetValue = startAngle,
                    animationSpec = tween(durationMillis = animateMillis)
                ) { currentRotation = value }
            }
        }

        rotate(rotation.value)
    }
)