package com.example.footballstatistics_app_android.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ButtonObject(onClick: () -> Unit, text : String, bgcolor : Color, textcolor : Color) {
    Button(
        onClick = { onClick() },
        modifier = Modifier
            .size(width = 200.dp, height = 60.dp)  // Custom size
            .padding(8.dp),  // Add border
        shape = RoundedCornerShape(16.dp),  // Rounded corners
        colors = ButtonDefaults.buttonColors(  // Custom colors
            containerColor = bgcolor,
            contentColor = textcolor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(text)
    }
}