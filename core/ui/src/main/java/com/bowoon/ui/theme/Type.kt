package com.bowoon.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

private val BaseFontFamily = FontFamily.Default

private fun movieAppTypography(
    fontWeight: FontWeight,
    fontSize: Int,
    lineHeight: Int
) = TextStyle(
    fontFamily = BaseFontFamily,
    fontWeight = fontWeight,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.Both
    ),
    platformStyle = androidx.compose.ui.text.PlatformTextStyle(
        includeFontPadding = false   // ✅ 핵심
    )
)

val MovieAppTypography = Typography(
    // 🎬 영화/TV 제목
    headlineLarge = movieAppTypography(
        fontWeight = FontWeight.Bold,
        fontSize = 30,
        lineHeight = 36
    ),
    headlineMedium = movieAppTypography(
        fontWeight = FontWeight.Bold,
        fontSize = 24,
        lineHeight = 30
    ),
    headlineSmall = movieAppTypography(
        fontWeight = FontWeight.Bold,
        fontSize = 20,
        lineHeight = 26
    ),
    // 📌 섹션 타이틀 (Overview, Facts, Cast, Images...)
    titleLarge = movieAppTypography(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20,
        lineHeight = 26
    ),
    titleMedium = movieAppTypography(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18,
        lineHeight = 24
    ),
    titleSmall = movieAppTypography(
        fontWeight = FontWeight.Medium,
        fontSize = 14,
        lineHeight = 20
    ),
    // 📖 본문
    bodyLarge = movieAppTypography(
        fontWeight = FontWeight.Normal,
        fontSize = 16,
        lineHeight = 22
    ),
    bodyMedium = movieAppTypography(
        fontWeight = FontWeight.Normal,
        fontSize = 14,
        lineHeight = 20
    ),
    bodySmall = movieAppTypography(
        fontWeight = FontWeight.Normal,
        fontSize = 12,
        lineHeight = 18
    ),
    // 🔖 메타/라벨 (E01, 평점, 보조텍스트)
    labelLarge = movieAppTypography(
        fontWeight = FontWeight.Medium,
        fontSize = 13,
        lineHeight = 18
    ),
    labelMedium = movieAppTypography(
        fontWeight = FontWeight.Medium,
        fontSize = 12,
        lineHeight = 16
    ),
    labelSmall = movieAppTypography(
        fontWeight = FontWeight.Medium,
        fontSize = 11,
        lineHeight = 14
    )
)