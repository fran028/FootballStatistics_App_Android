package com.example.footballstatistics_app_android.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Theme.LeagueGothic
import com.example.footballstatistics_app_android.Theme.white



@Composable
fun ViewTitle(title: String, image: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.25f))
                    )
                )
                .height(150.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 32.dp)
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = LeagueGothic,
                        fontSize = 48.sp,
                        color = white,
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 3f
                        )
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Image",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}