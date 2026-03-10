package com.cheeke.surfy.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp5

@Composable
fun MediaTitleComponent(
    media: Media
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dp16)
    ) {
        media.tagline.takeIf { !it?.trim().isNullOrEmpty() }?.let { tagLine ->
            Text(
                modifier = Modifier
                    .semantics {
                        contentDescription = "movieTagline"
                    }
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = tagLine,
                style = MaterialTheme.typography.labelMedium
            )
        }
        media.title.takeIf { !it?.trim().isNullOrEmpty() }?.let { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        media.originalTitle.takeIf { !it?.trim().isNullOrEmpty() }?.let { originalTitle ->
            Text(
                text = originalTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(height = dp5))

        val meta = when (media) {
            is Movie -> {
                buildString {
                    media.releaseDate?.let { append(it) }
                    media.certification?.let { if (isNotEmpty()) append(" · "); append(it) }
                    media.runtime?.let { if (isNotEmpty()) append(" · "); append("${it}분") }
                    if (!media.genres.isNullOrEmpty()) {
                        if (isNotEmpty()) {
                            append(" · ")
                        }
                        media.genres?.forEachIndexed { index, genre ->
                            if (index == media.genres?.lastIndex) {
                                append("${genre.name}")
                            } else {
                                append("${genre.name}, ")
                            }
                        }
                    }
                    media.voteAverage?.let { if (isNotEmpty()) append(" · "); append("★ ${"%.1f".format(it)}") }
                }
            }
            is Tv -> {
                buildString {
                    media.releaseDate?.let { append(it) }
                    if (!media.genres.isNullOrEmpty()) {
                        if (isNotEmpty()) {
                            append(" · ")
                        }
                        media.genres?.forEachIndexed { index, genre ->
                            if (index == media.genres?.lastIndex) {
                                append("${genre.name}")
                            } else {
                                append("${genre.name}, ")
                            }
                        }
                    }
                    media.voteAverage?.let { if (isNotEmpty()) append(" · "); append("★ ${"%.1f".format(it)}") }
                }
            }
            else -> ""
        }

        Text(
            text = meta,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}