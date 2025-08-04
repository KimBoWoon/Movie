package com.bowoon.ui.components

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.net.toUri
import com.bowoon.ui.utils.dp20

@Composable
fun ExternalIdLinkComponent(
    link: String,
    @DrawableRes resourceId: Int,
    contentDescription: String
) {
    val context = LocalContext.current

    link.takeIf { it.isNotEmpty() }?.let {
        Icon(
            modifier = Modifier
                .size(size = dp20)
                .clickable {
                    context.startActivity(Intent(Intent.ACTION_VIEW, link.toUri()))
                },
            painter = painterResource(id = resourceId),
            contentDescription = contentDescription
        )
    }
}