package com.bowoon.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

fun String?.matchedColorString(
    keyword: String,
    color: Color
): AnnotatedString = buildAnnotatedString {
    if (this@matchedColorString?.trim().isNullOrEmpty()) return@buildAnnotatedString
    append(this@matchedColorString)
    if (keyword.contains(other = keyword)) {
        keyword.indexOf(string = keyword).also { start ->
            addStyle(style = SpanStyle(color = color), start = start, end = start + keyword.length)
        }
    }
}