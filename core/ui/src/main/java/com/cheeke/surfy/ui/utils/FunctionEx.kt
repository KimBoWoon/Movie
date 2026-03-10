package com.cheeke.surfy.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import java.util.Locale

//fun String?.matchedColorString(
//    keyword: String,
//    color: Color
//): AnnotatedString = buildAnnotatedString {
//    if (this@matchedColorString?.trim().isNullOrEmpty()) return@buildAnnotatedString
//    append(this@matchedColorString)
//    if (keyword.contains(other = keyword)) {
//        keyword.indexOf(string = keyword).also { start ->
//            addStyle(style = SpanStyle(color = color), start = start, end = start + keyword.length)
//        }
//    }
//}

fun String?.matchedColorString(
    keyword: String,
    color: Color
): AnnotatedString {
    val text = this.orEmpty()
    val query = keyword.trim()

    // ✅ 키워드가 비어있으면 절대 하이라이트 X
    if (text.isBlank() || query.isBlank()) {
        return AnnotatedString(text = text)
    }

    val lowerText = text.lowercase(locale = Locale.getDefault())
    val lowerQuery = query.lowercase(locale = Locale.getDefault())

    return buildAnnotatedString {
        var start = 0
        while (true) {
            val idx = lowerText.indexOf(string = lowerQuery, startIndex = start)
            if (idx == -1) {
                append(text = text.substring(startIndex = start))
                break
            }

            append(text = text.substring(startIndex = start, endIndex = idx))

            withStyle(style = SpanStyle(color = color, fontWeight = FontWeight.SemiBold)) {
                append(text = text.substring(startIndex = idx, endIndex = idx + query.length))
            }

            start = idx + query.length
        }
    }
}

//fun String?.matchedColorStringOnce(keyword: String, color: Color): AnnotatedString {
//    val text = this.orEmpty()
//    val query = keyword.trim()
//    if (text.isBlank() || query.isBlank()) return AnnotatedString(text)
//
//    val idx = text.indexOf(query, ignoreCase = true)
//    if (idx == -1) return AnnotatedString(text)
//
//    return buildAnnotatedString {
//        append(text.substring(0, idx))
//        withStyle(SpanStyle(color = color)) { append(text.substring(idx, idx + query.length)) }
//        append(text.substring(idx + query.length))
//    }
//}