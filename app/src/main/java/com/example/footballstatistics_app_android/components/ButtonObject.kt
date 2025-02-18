package com.example.footballstatistics_app_android.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.footballstatistics_app_android.Theme.RobotoCondensed


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
                fontFamily = RobotoCondensed,
                color = textcolor, // Text color
                fontSize = 24.sp, // Text size // Text weight
            )
        )
    }
}