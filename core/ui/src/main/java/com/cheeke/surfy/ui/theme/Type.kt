package com.cheeke.surfy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

private val BaseFontFamily = FontFamily.Default

private fun surfyAppTypography(
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
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

val SurfyAppTypography = Typography(
    // 🎬 영화/TV 제목
    headlineLarge = surfyAppTypography(
        fontWeight = FontWeight.Bold,
        fontSize = 30,
        lineHeight = 36
    ),
    headlineMedium = surfyAppTypography(
        fontWeight = FontWeight.Bold,
        fontSize = 24,
        lineHeight = 30
    ),
    headlineSmall = surfyAppTypography(
        fontWeight = FontWeight.Bold,
        fontSize = 20,
        lineHeight = 26
    ),
    // 📌 섹션 타이틀 (Overview, Facts, Cast, Images...)
    titleLarge = surfyAppTypography(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20,
        lineHeight = 26
    ),
    titleMedium = surfyAppTypography(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18,
        lineHeight = 24
    ),
    titleSmall = surfyAppTypography(
        fontWeight = FontWeight.Medium,
        fontSize = 14,
        lineHeight = 20
    ),
    // 📖 본문
    bodyLarge = surfyAppTypography(
        fontWeight = FontWeight.Normal,
        fontSize = 16,
        lineHeight = 22
    ),
    bodyMedium = surfyAppTypography(
        fontWeight = FontWeight.Normal,
        fontSize = 14,
        lineHeight = 20
    ),
    bodySmall = surfyAppTypography(
        fontWeight = FontWeight.Normal,
        fontSize = 12,
        lineHeight = 18
    ),
    // 🔖 메타/라벨 (E01, 평점, 보조텍스트)
    labelLarge = surfyAppTypography(
        fontWeight = FontWeight.Medium,
        fontSize = 13,
        lineHeight = 18
    ),
    labelMedium = surfyAppTypography(
        fontWeight = FontWeight.Medium,
        fontSize = 12,
        lineHeight = 16
    ),
    labelSmall = surfyAppTypography(
        fontWeight = FontWeight.Medium,
        fontSize = 11,
        lineHeight = 14
    )
)