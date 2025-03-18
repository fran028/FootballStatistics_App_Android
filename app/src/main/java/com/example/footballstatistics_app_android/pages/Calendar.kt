package com.example.footballstatistics_app_android.pages

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Screen
import com.example.footballstatistics_app_android.Theme.LeagueGothic
import com.example.footballstatistics_app_android.Theme.RobotoCondensed
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.gray
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.components.ButtonIconObject
import com.example.footballstatistics_app_android.components.ViewTitle
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.data.UserRepository
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModelFactory
import com.example.footballstatistics_app_android.viewmodel.UserViewModel
import com.example.footballstatistics_app_android.viewmodel.UserViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.text.format

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarPage(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val matchRepository = MatchRepository(database.matchDao())
    val matchViewModelFactory = MatchViewModelFactory(matchRepository)
    val matchViewModel: MatchViewModel = viewModel(factory = matchViewModelFactory)
    val userRepository = UserRepository(database.userDao())
    val userViewModelFactory = UserViewModelFactory(userRepository)
    val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)
    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    val dateFormat = SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault())
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.UK)  // Example of format used in database

    var userid by remember { mutableStateOf("") }
    var shouldFetchMatches by remember { mutableStateOf(false) }
    var shouldFetchMonth by remember { mutableStateOf(true) }

    val loginUser by userViewModel.loginUser.collectAsState()

    LaunchedEffect(key1 = Unit) {
        userViewModel.getLoginUser()
    }

    LaunchedEffect(key1 = loginUser) {
        if (loginUser != null) {
            userid = userViewModel.loginUser.value!!.id.toString()
        }
    }

    LaunchedEffect(key1 = shouldFetchMonth, key2 = loginUser) {
        val firstDayOfMonth = currentMonth.clone() as Calendar
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        val lastDayOfMonth = currentMonth.clone() as Calendar
        lastDayOfMonth.set(
            Calendar.DAY_OF_MONTH,
            currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        )
        val startDateString = dateFormatter.format(firstDayOfMonth.time)
        val endDateString = dateFormatter.format(lastDayOfMonth.time)
        matchViewModel.getMatchesInMonth(
            startDateString,
            endDateString,
            userid
        )
        shouldFetchMonth = false
    }

    val matchesInMonth by matchViewModel.matchesInMonth.collectAsState()
    val dateList = remember { mutableStateListOf<String>() }

    LaunchedEffect(key1 = matchesInMonth) {
        for (match in matchesInMonth) {
            match?.let {
                val dateString = match.date
                val date = dateFormatter.parse(dateString) // Parse the string to a Date object
                if (date != null) {
                    val formattedDate = dateFormatter.format(date) // Format the Date object
                    dateList.add(formattedDate)
                }
            }
        }
    }

    LaunchedEffect(key1 = userViewModel.loginUser, key2 = shouldFetchMatches) {
        if (selectedDate != null && shouldFetchMatches) {
            matchViewModel.getMatchesBetweenDates(
                dateFormatter.format(selectedDate!!),
                dateFormatter.format(selectedDate!!),
                userViewModel.loginUser.value?.id.toString()
            )
            shouldFetchMatches = false
        }
    }
    val dateMatches by matchViewModel.matchesBetweenDates.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .verticalScroll(scrollState) ,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "CALENDAR", image = R.drawable.calendar_img, navController = navController)
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .width(40.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(white)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Day(
                        day = 10,
                        isSelected = true,
                        onDateSelected = {  },
                        hasMatch = false,
                        isToday = false
                    )
                }
                Text(
                    text = "SELECTED",
                    fontFamily = LeagueGothic,
                    fontSize = 24.sp,
                    color = white
                )
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .width(40.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(blue)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Day(
                        day = 10,
                        isSelected = false,
                        onDateSelected = { },
                        hasMatch = true,
                        isToday = false
                    )
                }
                Text(
                    text = "MATCH",
                    fontFamily = LeagueGothic,
                    fontSize = 24.sp,
                    color = white,
                )
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .width(40.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(gray)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Day(
                        day = 10,
                        isSelected = false,
                        onDateSelected = { },
                        hasMatch = false,
                        isToday = true
                    )
                }
                Text(
                    text = "TODAY",
                    fontFamily = LeagueGothic,
                    fontSize = 24.sp,
                    color = white,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
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
                    .height(400.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    MonthHeader(
                        currentMonth = currentMonth,
                        onPreviousMonth = {
                            val newMonth = currentMonth.clone() as Calendar
                            newMonth.add(Calendar.MONTH, -1)
                            currentMonth = newMonth
                            shouldFetchMonth = true
                        },
                        onNextMonth = {
                            val newMonth = currentMonth.clone() as Calendar
                            newMonth.add(Calendar.MONTH, 1)
                            currentMonth = newMonth
                            shouldFetchMonth = true
                        }
                    )
                    CalendarGrid(
                        currentMonth = currentMonth,
                        selectedDate = selectedDate,
                        onDateSelected = {
                            selectedDate = it
                            shouldFetchMatches = true
                        },
                        matchViewModel = matchViewModel,
                        userViewModel = userViewModel,
                        userid = userid,
                        matchesInMonth = dateList
                    )
                }
            }
        }
        if(selectedDate != null){
            Column {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "MATCHES ON ${dateFormat.format(selectedDate)}",
                    fontFamily = LeagueGothic,
                    fontSize = 40.sp,
                    color = white,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column (Modifier.padding(horizontal = 32.dp )) {
                    if(dateMatches.isEmpty()){
                        Spacer(modifier = Modifier.height(8.dp))
                        ButtonIconObject(
                            text = "No matches found",
                            bgcolor = white,
                            height = 50.dp,
                            textcolor = black,
                            value = "",
                            icon = R.drawable.soccer,
                            onClick = {  }
                        )
                    } else {
                        for (match in dateMatches) {
                            if (match != null) {
                                ButtonIconObject(
                                    text = "Match ${match.id}",
                                    onClick = { navController.navigate(Screen.Match.createRoute(match.id)) },
                                    bgcolor = blue,
                                    height = 50.dp,
                                    textcolor = black,
                                    icon = R.drawable.soccer,
                                    value = match.date
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
        /*
        Column {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "MATCHES PLAYED THIS MONTH",
                fontFamily = LeagueGothic,
                fontSize = 40.sp,
                color = white,
                modifier = Modifier.padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Column (Modifier.padding(horizontal = 32.dp )) {
                for (i in 1..5) {
                    ButtonIconObject(
                        text = "Match $i",
                        onClick = { navController.navigate(Screen.Match.route) },
                        bgcolor = blue,
                        height = 50.dp,
                        textcolor = black,
                        icon = R.drawable.soccer,
                        value = "20/02/2025"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }*/
    }
}



@Composable
fun MonthHeader(
    currentMonth: Calendar,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
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
                fontSize = 32.sp,
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
    onDateSelected: (java.util.Date) -> Unit,
    matchViewModel: MatchViewModel,
    userViewModel: UserViewModel,
    userid: String,
    matchesInMonth: List<String>,
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
            .height(550.dp)
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
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
            Day(
                day = day,
                isSelected = isSameDay(date.time, selectedDate),
                onDateSelected = { onDateSelected(date.time) },
                hasMatch = if(matchesInMonth.contains(dateFormatter.format(date.time))) true else false,
                isToday = isSameDay(date.time, Date())
            )
        }

    }


}

@Composable
fun Day(day: Int, isSelected: Boolean, onDateSelected: () -> Unit, hasMatch: Boolean = false, isToday: Boolean = false) {
    if(hasMatch){
        Log.d("Day", "Day has match: $day")
    }
    Box(
        modifier = Modifier
            .padding(4.dp)
            .width(40.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) white else if (hasMatch) black else if (isToday) gray else Color.Transparent)
            .clickable { onDateSelected() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color =  if (isSelected || hasMatch) blue else black,
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
            color = black,
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
