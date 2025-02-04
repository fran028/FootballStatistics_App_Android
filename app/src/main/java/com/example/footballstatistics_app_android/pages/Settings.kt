package com.example.footballstatistics_app_android.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.components.ViewTitle

//val LocalAppTheme = compositionLocalOf { AppTheme() }

@Composable
fun SettingPage(modifier: Modifier = Modifier, navController: NavController, updateSelectedItemIndex: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize().background(black),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "SETTINGS", image = R.drawable.setting_poster)
        Spacer(modifier = Modifier.height(8.dp))
        Text("This is a sample screen layout.", color = white, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(32.dp))
        ButtonObject(
            onClick = { /* Handle button click */ },
            text = "Click Me",
            bgcolor = Color(0xff59834a),
            textcolor = white,
            width = 200.dp,
            height = 50.dp

        )
    }
}