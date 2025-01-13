package com.bowoon.ui.collaps

abstract class FixedScrollFlagState(heightRange: IntRange) : ScrollFlagState(heightRange) {
    final override val offset: Float = 0f
}