package com.example.footballstatistics_app_android.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
//import com.example.footballstatistics_app_android.Theme.AppTheme
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.red
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.components.ViewTitle

//val LocalAppTheme = compositionLocalOf { AppTheme() }

@Composable
fun SettingPage(modifier: Modifier = Modifier, navController: NavController, updateSelectedItemIndex: (Int) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "SETTINGS", image = R.drawable.setting_poster)
        Spacer(modifier = Modifier.height(16.dp))
        ButtonObject(
            onClick = { },
            text = "Settings",
            bgcolor = red,
            textcolor = black,
            width = 500.dp,
            height = 60.dp,
        )
    }
}