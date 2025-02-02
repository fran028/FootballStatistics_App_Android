package com.example.footballstatistics_app_android.pages

import android.provider.ContactsContract.Profile
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.footballstatistics_app_android.AppTheme
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.green
import com.example.footballstatistics_app_android.Theme.red
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.components.ColorBar
import com.example.footballstatistics_app_android.components.ViewTitle

//val LocalAppTheme = compositionLocalOf { AppTheme() }

@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController, updateSelectedItemIndex: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize().background(black),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "Profile", image = R.drawable.profile_img)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Franco Scarpettini ",
            fontSize = 40.sp,
            fontWeight = FontWeight.SemiBold,
            color = white,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "25 Years",
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = white,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(
                text = "175cm ",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = white,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = "60Kg ",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = white,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            ColorBar(
                text = "MATCHES ",
                value = "10 MP",
                bgcolor = green,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorBar(
                text = "DISTANCE ",
                value = "50 km",
                bgcolor = yellow,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorBar(
                text = "HEARTRATE ",
                value = "159 bpm",
                bgcolor = red,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorBar(
                text = "TIME ",
                value = "10h 12m",
                bgcolor = blue,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "PR",
            fontSize = 40.sp,
            fontWeight = FontWeight.SemiBold,
            color = white,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

