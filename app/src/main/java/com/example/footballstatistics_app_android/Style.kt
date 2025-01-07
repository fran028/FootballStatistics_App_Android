package com.example.footballstatistics_app_android

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class AppColors(

    val black: Color = Color(0xff242424),
    val white: Color = Color(0xffe6e6e6),
    val red: Color = Color(0xfff94545),
    val blue: Color = Color(0xff7da8e8),
    val green: Color = Color(0xff59834a),
    val yellow: Color = Color(0xfffdcb60)
)

data class AppTypography(
    val title: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    val content: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)


data class AppTheme(
    val colors: AppColors = AppColors(),
    val typography: AppTypography = AppTypography()
)


