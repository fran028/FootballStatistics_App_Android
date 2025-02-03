package com.example.footballstatistics_app_android.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Screen
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.components.ButtonItem
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.components.DynamicButtonList
import com.example.footballstatistics_app_android.components.ViewTitle

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, updateSelectedItemIndex: (Int) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "Fulbo Stats", R.drawable.home_img)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Matched Played",
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        val myButtonItems = listOf(
            ButtonItem(1, "Match 1", {updateSelectedItemIndex(4)} , yellow, width = 500.dp, height = 60.dp),
            ButtonItem(1, "Match 2", {updateSelectedItemIndex(4)} , yellow, width = 500.dp, height = 60.dp),
            ButtonItem(1, "Match 3", {updateSelectedItemIndex(4)} , yellow, width = 500.dp, height = 60.dp),
        )
        DynamicButtonList(buttonItems = myButtonItems)


    }
}
