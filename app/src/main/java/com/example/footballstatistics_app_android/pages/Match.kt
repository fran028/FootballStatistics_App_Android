package com.example.footballstatistics_app_android.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Theme.LeagueGothic
import com.example.footballstatistics_app_android.Theme.RobotoCondensed
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.green
import com.example.footballstatistics_app_android.Theme.red
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.components.ColorBar
import com.example.footballstatistics_app_android.components.RecordBox
import com.example.footballstatistics_app_android.components.StatBox
import com.example.footballstatistics_app_android.components.ViewTitle

@Composable
fun MatchPage(modifier: Modifier = Modifier, navController: NavController, updateSelectedItemIndex: (Int) -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "MATCH", image = R.drawable.match_img)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "03/02/2025 ",
            fontFamily = RobotoCondensed,
            fontSize = 36.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "00h:58m:32s",
            fontFamily = RobotoCondensed,
            fontSize = 36.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
            ) {
                StatBox(
                    text = "VELOCITY",
                    value = "26 km/h",
                    avg = "Avg: 15 km/h",
                    bgcolor = blue,
                    textcolor = white,
                    height = 100.dp,
                    width = 100.dp
                )
                StatBox(
                    text = "DISTANCE ",
                    value = "7,3 km",
                    avg = "9:48m/km",
                    bgcolor = yellow,
                    textcolor = white,
                    height = 100.dp,
                    width = 100.dp
                )
                StatBox(
                    text = "HEARTRATE ",
                    value = "190 bpm",
                    avg = "Avg: 169 bpm",
                    bgcolor = red,
                    textcolor = white,
                    height = 100.dp,
                    width = 100.dp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chart: Example",
            fontFamily = RobotoCondensed,
            fontSize = 32.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column (Modifier.padding(horizontal = 36.dp )){
            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
                    .border(width = 4.dp, color = white, shape = RoundedCornerShape(8.dp), )
                    .height(125.dp),
                contentAlignment = Alignment.Center
            ) {

            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "STATISTICS (Example)",
            fontFamily = LeagueGothic,
            fontSize = 32.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)) {
            ColorBar(
                text = "DEFENSIVE ACTIONS",
                value = "30",
                bgcolor = yellow,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorBar(
                text = "ATTACKING ACTION",
                value = "27",
                bgcolor = green,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorBar(
                text = "JUMPS ",
                value = "10",
                bgcolor = blue,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorBar(
                text = "FALL/FOULS ",
                value = "5",
                bgcolor = red,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
        }
    }
}