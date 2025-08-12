package com.bowoon.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.bowoon.common.Log

enum class ButtonState { Pressed, Idle }

fun Modifier.bounceClick(
    onClick: (() -> Unit)? = null
) = composed {
    var buttonState by remember { mutableStateOf(value = ButtonState.Idle) }
    val scale by animateFloatAsState(targetValue = if (buttonState == ButtonState.Pressed) 0.95f else 1f)
    var clicked by remember { mutableStateOf<Boolean>(value = false) }

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onClick?.let { it() } }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }/*.toggleable(
            value = clicked,
            onValueChange = { state ->
                clicked = state
                Log.d("toggle -> $state")
            }
        )*/
}

fun Modifier.bottomLineBorder(
    strokeWidth: Dp,
    color: Color
) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width, y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

fun Modifier.topLineBorder(
    strokeWidth: Dp,
    color: Color
) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = width, y = 0f),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

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