package com.example.footballstatistics_app_android.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.footballstatistics_app_android.AppTheme

val LocalAppTheme = compositionLocalOf { AppTheme() }

@Composable
fun SettingPage(modifier: Modifier = Modifier) {
    val theme = LocalAppTheme.current
    Column (modifier = modifier.fillMaxSize()
        .background(Color(0xff242424)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Settings",
            style = theme.typography.title,

        )
    }
}