package com.example.footballstatistics_app_android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.footballstatistics_app_android.theme.RobotoCondensed
import com.example.footballstatistics_app_android.theme.black
import com.example.footballstatistics_app_android.theme.white

@Composable
fun RecordBox(
    icon: Int = 0,
    text: String,
    value: String,
    bgcolor: Color,
    textcolor: Color,
    height: Dp,
    width: Dp,
){
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .width(width),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            fontFamily = RobotoCondensed,
            color = white,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,

        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier
            .width(width)
            .clip(RoundedCornerShape(8.dp))
            .background(bgcolor)
            .height(height),
            contentAlignment = Alignment.Center)
        {
            Text(
                text = value,
                fontFamily = RobotoCondensed,
                color = black,
                fontSize = 16.sp,
            )
        }

    }

}
