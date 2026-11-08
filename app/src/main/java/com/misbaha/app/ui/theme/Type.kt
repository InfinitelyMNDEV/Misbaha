package com.misbaha.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.misbaha.app.R

val Tajawal = FontFamily(
    Font(R.font.tajawal_regular, FontWeight.Normal),
    Font(R.font.tajawal_medium, FontWeight.Medium),
    Font(R.font.tajawal_bold, FontWeight.Bold)
)

val MisbahaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Bold,
        fontSize = 52.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 56.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 48.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 46.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 42.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Tajawal,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    )
)
