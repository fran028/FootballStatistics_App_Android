package com.example.footballstatistics_app_android.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.components.ViewTitle

@Composable
fun HomePage(modifier: Modifier = Modifier) {

    Column(
        modifier = Modifier
            .fillMaxSize().background(Color(0xff242424)),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "Fulbo Stats", R.drawable.home_img)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Matched Played",
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        ButtonObject(
            onClick = { /* Handle button click */ },
            text = "Click Me",
            bgcolor = Color(0xff59834a),
            textcolor = Color.White
        )
    }
}
