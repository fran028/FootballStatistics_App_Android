package com.example.footballstatistics_app_android.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ButtonItem(
    val id: Int,
    val label: String,
    val onClick: () -> Unit,
    val color: Color,
    val width: Dp,
    val height: Dp)

@Composable
fun DynamicButtonList(
    buttonItems: List<ButtonItem>,
) {
    LazyColumn(
        modifier = Modifier.padding(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(buttonItems) { item ->
            ButtonObject(
                onClick = {item.onClick() },
                text = item.label,
                bgcolor = item.color ,
                textcolor = Color(0xff242424),
                width = item.width,
                height = item.height,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}