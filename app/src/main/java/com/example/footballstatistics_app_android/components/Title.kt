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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.footballstatistics_app_android.R

@Composable
fun ViewTitle(title: String, image: Int) {
    Box( // Use a Box to layer elements
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) { // Box doesn't have background property, so we use Modifier on it's children.
        Image(
            painter = painterResource(id = image),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(), // Image fills the Box
            contentScale = ContentScale.Crop
        )
        Box( // This Box will contain the gradient and other content
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                    )
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, // Use SpaceBetween for logo
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize() // Row fills the gradient Box
                    .padding(16.dp) // Add padding for the content
            ) {
                Text(
                    text = title,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
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