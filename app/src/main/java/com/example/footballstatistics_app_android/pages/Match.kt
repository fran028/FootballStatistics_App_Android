package com.example.footballstatistics_app_android.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.components.ViewTitle

@Composable
fun MatchPage(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize().background(Color(0xff242424)),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "Match", image = R.drawable.match_img)
        Spacer(modifier = Modifier.height(8.dp))
        Text("This is a sample screen layout.", color = Color.White, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(32.dp))
        ButtonObject(
            onClick = { /* Handle button click */ },
            text = "Click Me",
            bgcolor = Color(0xff59834a),
            textcolor = Color.White
        )
    }
}