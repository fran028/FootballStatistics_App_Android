package com.example.footballstatistics_app_android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@Composable
fun ButtonObject(onClick: () -> Unit, text : String, color : Color) {
    Button(onClick = { onClick() }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.Green,
        contentColor = Color.White
    )) {
        Text(text)
    }
}