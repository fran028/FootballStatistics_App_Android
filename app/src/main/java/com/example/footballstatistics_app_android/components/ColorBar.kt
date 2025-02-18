package com.example.footballstatistics_app_android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.footballstatistics_app_android.theme.LeagueGothic
import com.example.footballstatistics_app_android.theme.RobotoCondensed

@Composable
fun ColorBar(
    icon: Int = 0,
    text: String,
    value: String,
    bgcolor: Color,
    textcolor: Color,
    width: Dp,
    height: Dp,
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(bgcolor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontFamily = RobotoCondensed,
                style = TextStyle(
                    color = textcolor,
                    fontSize = 22.sp,
                ),
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Text(
                text = value,
                fontFamily = LeagueGothic,
                style = TextStyle(
                    color = textcolor,
                    fontSize = 24.sp,
                ),
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }
    }

}