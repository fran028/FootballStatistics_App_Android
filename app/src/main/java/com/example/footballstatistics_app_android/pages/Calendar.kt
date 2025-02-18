package com.example.footballstatistics_app_android.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.theme.LeagueGothic
import com.example.footballstatistics_app_android.theme.RobotoCondensed
import com.example.footballstatistics_app_android.theme.black
import com.example.footballstatistics_app_android.theme.blue
import com.example.footballstatistics_app_android.theme.gray
import com.example.footballstatistics_app_android.theme.white
import com.example.footballstatistics_app_android.components.ViewTitle
import java.text.SimpleDateFormat
import java.util.Calendar

@Composable
fun CalendarPage(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    var selectedDate by remember { mutableStateOf<java.util.Date?>(null) }
    val currentMonth by remember { mutableStateOf(Calendar.getInstance()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "CALENDAR", image = R.drawable.calendar_img)
        Spacer(modifier = Modifier.height(64.dp))
        Column(Modifier.padding(horizontal = 36.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
                    .border(
                        width = 4.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .height(500.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    MonthHeader(
                        currentMonth = currentMonth,
                        onPreviousMonth = {
                            currentMonth.add(Calendar.MONTH, -1)
                        },
                        onNextMonth = {
                            currentMonth.add(Calendar.MONTH, 1)
                        }
                    )
                    CalendarGrid(
                        currentMonth = currentMonth,
                        selectedDate = selectedDate,
                        onDateSelected = { selectedDate = it }
                    )
                }
            }
        }
    }
}

@Composable
fun MonthHeader(
    currentMonth: Calendar,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val monthName = SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault()).format(currentMonth.time)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    tint = black
                )
            }
            Text(
                text = monthName,
                fontFamily = LeagueGothic,
                fontSize = 24.sp,
                color = black,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
            )
            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = black
                )
            }
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: Calendar,
    selectedDate: java.util.Date?,
    onDateSelected: (java.util.Date) -> Unit
) {
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = currentMonth.clone() as Calendar
    firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)
    val days = (1..daysInMonth).toList()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(blue)
            .height(450.dp)
    ) {
        items(items = getDayLabels()) { dayLabel ->
            DayLabel(dayLabel = dayLabel)
        }
        items(items = getEmptyDays(firstDayOfWeek)) {
            EmptyDay()
        }
        items(items = days) { day ->
            val date = firstDayOfMonth.clone() as Calendar
            date.set(Calendar.DAY_OF_MONTH, day)
            Day(
                day = day,
                isSelected = isSameDay(date.time, selectedDate),
                onDateSelected = { onDateSelected(date.time) }
            )
        }
    }
}

@Composable
fun Day(day: Int, isSelected: Boolean, onDateSelected: () -> Unit, hasMatch: Boolean = false) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .width(40.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) white else Color.Transparent)
            .clickable { onDateSelected() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = if(hasMatch) blue else if (isSelected) gray else white,
            textAlign = TextAlign.Center,
            fontFamily = RobotoCondensed,
        )
    }
}

@Composable
fun EmptyDay() {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .width(40.dp)
            .height(40.dp)
    )
}

@Composable
fun DayLabel(dayLabel: String) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .width(40.dp)
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayLabel,
            color = white,
            textAlign = TextAlign.Center,
            fontFamily = RobotoCondensed
        )
    }
}

fun getDayLabels(): List<String> {
    return listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
}

fun getEmptyDays(firstDayOfWeek: Int): List<Int> {
    return when (firstDayOfWeek) {
        Calendar.MONDAY -> (1 downTo 0).toList()
        Calendar.TUESDAY -> (1..1).toList()
        Calendar.WEDNESDAY -> (1..2).toList()
        Calendar.THURSDAY -> (1..3).toList()
        Calendar.FRIDAY -> (1..4).toList()
        Calendar.SATURDAY -> (1..5).toList()
        Calendar.SUNDAY -> (1..6).toList()
        else -> emptyList()
    }
}

fun isSameDay(date1: java.util.Date, date2: java.util.Date?): Boolean {
    if (date2 == null) return false
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal1.time = date1
    cal2.time = date2
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

fun dayHasMatch(date1: java.util.Date): Boolean{

    return true
}