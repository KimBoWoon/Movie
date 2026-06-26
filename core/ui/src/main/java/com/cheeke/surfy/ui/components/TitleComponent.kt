package com.cheeke.surfy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp24
import com.cheeke.surfy.ui.utils.dp40
import com.cheeke.surfy.ui.utils.dp5

@Composable
fun TitleComponent(
    title: String = "",
    isFavorite: Boolean,
    animatedAlpha: Float = -1f,
    goToBack: (() -> Unit)? = null,
    onFavorite: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(color = MaterialTheme.colorScheme.surface.copy(alpha = animatedAlpha))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            goToBack?.let {
                Surface(
                    modifier = Modifier
                        .padding(start = dp10, top = dp10, bottom = dp5)
                        .size(size = dp40),
                    onClick = it,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "goToBack",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(size = dp24)
                        )
                    }
                }
            }

            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(weight = 1f)
                        .then(
                            other = if (animatedAlpha != -1f) {
                                Modifier.alpha(alpha = animatedAlpha)
                            } else {
                                Modifier
                            }
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            } else {
                Spacer(modifier = Modifier.weight(weight = 1f))
            }

            onFavorite?.let {
                Surface(
                    modifier = Modifier
                        .padding(end = dp10, top = dp10, bottom = dp5)
                        .size(size = dp40),
                    onClick = it,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        FavoriteButtonComponent(
                            modifier = Modifier.size(size = dp24),
                            isFavorite = isFavorite,
                            onClick = it
                        )
                    }
                }
            }
        }
    }
}