package com.bowoon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bowoon.ui.dp1
import com.bowoon.ui.dp10
import com.bowoon.ui.dp25
import com.bowoon.ui.dp50

@Composable
fun BoxScope.ScrollToTopComponent(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(end = dp10, bottom = dp10)
            .size(dp50)
            .background(color = Color.White, shape = CircleShape)
            .border(width = dp1, color = Color.LightGray, shape = CircleShape)
            .align(Alignment.BottomEnd)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = dp25)
            ) { onClick() }
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = "ScrollToTopIcon",
            tint = Color.Black
        )
    }
}