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
import com.example.footballstatistics_app_android.Screen
import com.example.footballstatistics_app_android.Theme.LeagueGothic
import com.example.footballstatistics_app_android.Theme.RobotoCondensed
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.components.ViewTitle

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "FULBO STATS", R.drawable.home_img)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Last Match Played",
            fontFamily = LeagueGothic,
            fontSize = 40.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "03/02/2025",
            fontFamily = RobotoCondensed,
            fontSize = 32.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chart: Example",
            fontFamily = LeagueGothic,
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

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Matches List",
            fontFamily = LeagueGothic,
            fontSize = 32.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        for (i in 1..5) {
            ButtonObject(
                text = "Match $i",
                onClick = {navController.navigate(Screen.Match.route)},
                bgcolor = yellow,
                width = 500.dp,
                height = 60.dp,
                textcolor = black,
            )
        }

//        val myButtonItems = listOf(
//            ButtonItem(1, "Match 1", {navController.navigate(Screen.Match.route)} , yellow, width = 500.dp, height = 60.dp),
//            ButtonItem(1, "Match 2", {navController.navigate(Screen.Match.route)} , yellow, width = 500.dp, height = 60.dp),
//            ButtonItem(1, "Match 3", {navController.navigate(Screen.Match.route)} , yellow, width = 500.dp, height = 60.dp),
//            ButtonItem(1, "Match 4", {navController.navigate(Screen.Match.route)} , yellow, width = 500.dp, height = 60.dp),
//            ButtonItem(1, "Match 5", {navController.navigate(Screen.Match.route)} , yellow, width = 500.dp, height = 60.dp),
//
//            )
//        //Column(modifier = Modifier.verticalScroll(scrollState)) {
//            DynamicButtonList(buttonItems = myButtonItems)
//        //}

    }
}
