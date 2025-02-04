package com.example.footballstatistics_app_android.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.components.ViewTitle

@Composable
fun CalendarPage(modifier: Modifier = Modifier, navController: NavController, updateSelectedItemIndex: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize().background(black),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "CALENDAR", image = R.drawable.calendar_img)
        Spacer(modifier = Modifier.height(64.dp))
        Column (Modifier.padding(horizontal = 36.dp )){
            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
                    .border(width = 4.dp, color = Color.Transparent, shape = RoundedCornerShape(8.dp), )
                    .height(500.dp),
            ) {
                Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(white)
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "Month",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = black,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(blue)
                            .height(450.dp),
                    )
                }
            }
        }
    }
}