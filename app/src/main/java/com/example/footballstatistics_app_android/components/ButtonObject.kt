package com.example.footballstatistics_app_android.components

import android.icu.text.ListFormatter.Width
import android.provider.CalendarContract.Colors
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.white


@Composable
fun ButtonObject(
    onClick: () -> Unit,
    text: String,
    bgcolor: Color,
    textcolor: Color,
    width: Dp,
    height: Dp,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(width = width, height = height)  // Custom size
            .padding(8.dp),  // Add border
        shape = RoundedCornerShape(8.dp),  // Rounded corners
        colors = ButtonDefaults.buttonColors(  // Custom colors
            containerColor = bgcolor,
            contentColor = textcolor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = textcolor, // Text color
                fontSize = 24.sp, // Text size
                fontWeight = FontWeight.Bold, // Text weight
            )
        )
    }
}