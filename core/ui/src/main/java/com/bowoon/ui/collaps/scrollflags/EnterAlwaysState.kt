package com.bowoon.ui.collaps.scrollflags

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.setValue
import com.bowoon.ui.collaps.ScrollFlagState

class EnterAlwaysState(
    heightRange: IntRange,
    scrollOffset: Float = 0f
) : ScrollFlagState(heightRange) {

    override var _scrollOffset by mutableFloatStateOf(
        scrollOffset.coerceIn(
            0f,
            maxHeight.toFloat()
        )
    )

    override val offset: Float
        get() = -(scrollOffset - rangeDifference).coerceIn(0f, minHeight.toFloat())

    override var scrollOffset: Float
        get() = _scrollOffset
        set(value) {
            val oldOffset = _scrollOffset
            _scrollOffset = value.coerceIn(0f, maxHeight.toFloat())
            _consumed = oldOffset - _scrollOffset
        }

    companion object {
        val Saver = run {
            val minHeightKey = "MinHeight"
            val maxHeightKey = "MaxHeight"
            val scrollOffsetKey = "ScrollOffset"

            mapSaver(
                save = {
                    mapOf(
                        minHeightKey to it.minHeight,
                        maxHeightKey to it.maxHeight,
                        scrollOffsetKey to it.scrollOffset
                    )
                },
                restore = {
                    EnterAlwaysState(
                        heightRange = (it[minHeightKey] as Int)..(it[maxHeightKey] as Int),
                        scrollOffset = it[scrollOffsetKey] as Float,
                    )
                }
            )
        }
    }
}