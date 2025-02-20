package com.example.footballstatistics_app_android.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.footballstatistics_app_android.Theme.LeagueGothic
import com.example.footballstatistics_app_android.Theme.RobotoCondensed
import com.example.footballstatistics_app_android.Theme.black

@Composable
fun StatBox(
    onclick: () -> Unit = {},
    icon: Int = 0,
    text: String,
    value: String,
    avg: String,
    bgcolor: Color,
    textcolor: Color,
    height: Dp,
    width: Dp,
    selected: Boolean = false
    ){
        val actualHeight = if(selected) height + 10.dp else height
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(width).clickable { onclick() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontFamily = LeagueGothic,
                style = TextStyle(
                    color = textcolor,
                    fontSize = 16.sp,
                ),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(width)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgcolor)
                    .height(actualHeight)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            )
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = "Soccer Image",
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = value,
                        fontFamily = RobotoCondensed,
                        style = TextStyle(
                            color = black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = avg,
                        fontFamily = RobotoCondensed,
                        style = TextStyle(
                            color = black,
                            fontSize = 14.sp
                        )
                    )
                }
            }

        }
}