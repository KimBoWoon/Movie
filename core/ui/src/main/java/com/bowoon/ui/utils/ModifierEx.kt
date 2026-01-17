package com.bowoon.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.io.path.moveTo

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

fun Modifier.border(
    line: Line,
    strokeWidth: Dp,
    color: Color
) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2

            when (line) {
                Line.START -> {
                    drawLine(
                        color = color,
                        start = Offset(x = 0f, y = 0f),
                        end = Offset(x = 0f, y = height),
                        strokeWidth = strokeWidthPx
                    )
                }
                Line.TOP -> {
                    drawLine(
                        color = color,
                        start = Offset(x = 0f, y = 0f),
                        end = Offset(x = width, y = 0f),
                        strokeWidth = strokeWidthPx
                    )
                }
                Line.END -> {
                    drawLine(
                        color = color,
                        start = Offset(x = width, y = 0f),
                        end = Offset(x = width, y = height),
                        strokeWidth = strokeWidthPx
                    )
                }
                Line.BOTTOM -> {
                    drawLine(
                        color = color,
                        start = Offset(x = 0f, y = height),
                        end = Offset(x = width, y = height),
                        strokeWidth = strokeWidthPx
                    )
                }
            }
        }
    }
)

enum class Line {
    START, TOP, END, BOTTOM
}

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

fun Modifier.topRoundedBorder(
    strokeWidth: Dp,
    color: Color,
    cornerRadius: Dp
) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }
        val cornerRadiusPx = density.run { cornerRadius.toPx() }

        drawBehind {
            val width = size.width
            val height = size.height

            val path = Path().apply {
                // 좌측 하단에서 시작
                moveTo(0f, height)
                // 좌측 상단으로 이동 (라운드 시작 전까지)
                lineTo(0f, cornerRadiusPx)
                // 좌측 상단 라운드
                arcTo(
                    rect = Rect(0f, 0f, cornerRadiusPx * 2, cornerRadiusPx * 2),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                // 상단 라인 (우측 상단 라운드 전까지)
                lineTo(width - cornerRadiusPx, 0f)
                // 우측 상단 라운드
                arcTo(
                    rect = Rect(width - cornerRadiusPx * 2, 0f, width, cornerRadiusPx * 2),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                // 우측 하단으로 이동
                lineTo(width, height)
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = strokeWidthPx)
            )
        }
    }
)