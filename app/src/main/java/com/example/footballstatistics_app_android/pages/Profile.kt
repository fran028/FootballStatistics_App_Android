package com.example.footballstatistics_app_android.pages

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.footballstatistics_app_android.Theme.green
import com.example.footballstatistics_app_android.Theme.red
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.components.ColorBar
import com.example.footballstatistics_app_android.components.RecordBox
import com.example.footballstatistics_app_android.components.ViewTitle
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.data.User
import com.example.footballstatistics_app_android.data.UserRepository
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModelFactory
import com.example.footballstatistics_app_android.viewmodel.UserViewModel
import com.example.footballstatistics_app_android.viewmodel.UserViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController) {
    /*if (user.isLoggedIn == false) {
        navController.navigate(Screen.Login.route) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }*/



    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val appDatabase = AppDatabase.getDatabase(context)
    val userRepository = UserRepository(appDatabase.userDao())
    val viewModelFactory = UserViewModelFactory(userRepository)
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory)
    val matchRepository = MatchRepository(appDatabase.matchDao())
    val matchViewModelFactory = MatchViewModelFactory(matchRepository)
    val matchViewModel: MatchViewModel = viewModel(factory = matchViewModelFactory)
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var routeToNavigate by remember { mutableStateOf("") }
    var shouldNavigate by remember { mutableStateOf(false) }

    fun navigateWithLoading(route: String) {
        isLoading = true
        routeToNavigate = route
        shouldNavigate = false
        coroutineScope.launch {
            delay(2000)
            isLoading = false
            shouldNavigate = true
        }
    }

    LaunchedEffect(key1 = Unit) {
        userViewModel.getLoginUser()
        matchViewModel.getMatchCount(userViewModel.loginUser.toString())
        matchViewModel.getTotalDuration()
    }
    val getUser by userViewModel.loginUser.collectAsState()
    val getMatchCount by matchViewModel.matchCount.collectAsState()
    val getTotalDuration by matchViewModel.totalDuration.collectAsState()


    val user = getUser ?: User(
        id = "",
        username = "",
        password = "",
        fullName = "",
        height = 0,
        weight = 0,
        date = ""
    )
    val name = user.fullName
    val age = calculateAge(user.date)
    val height = user.height
    val weight = user.weight
    val stayLogin = user.isLoggedIn



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .verticalScroll(scrollState) ,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "PROFILE", image = R.drawable.profile_img)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = name,
            fontFamily = RobotoCondensed,
            fontSize = 36.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$age",
            fontFamily = RobotoCondensed,
            fontSize = 32.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(horizontal = 32.dp)) {
            Text(
                text = "${height}cm ",
                fontFamily = RobotoCondensed,
                fontSize = 24.sp,
                color = white
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${weight}kg ",
                fontFamily = RobotoCondensed,
                fontSize = 24.sp,
                color = white
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)) {
            ColorBar(
                text = "MATCHES ",
                value = "$getMatchCount MP",
                bgcolor = green,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorBar(
                text = "DISTANCE ",
                value = "50 km",
                bgcolor = yellow,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorBar(
                text = "TIME ",
                value = getTotalDuration,
                bgcolor = blue,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "PERSONAL RECORDS",
                fontFamily = LeagueGothic,
                fontSize = 32.sp,
                color = white,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 0.dp)
            )
            Text(
                text = "(In one match)",
                fontFamily = RobotoCondensed,
                fontSize = 12.sp,
                color = green,
                modifier = Modifier.padding(horizontal = 0.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
            ) {
                RecordBox(
                    text = "TOP\n SPEED ",
                    value = "26 km/h",
                    bgcolor = blue,
                    textcolor = white,
                    height = 100.dp,
                    width = 100.dp
                )
                RecordBox(
                    text = "TOP\n DISTANCE ",
                    value = "7,3 km",
                    bgcolor = yellow,
                    textcolor = white,
                    height = 100.dp,
                    width = 100.dp
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "SETTINGS",
            fontFamily = LeagueGothic,
            fontSize = 32.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Column (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)){
            ButtonObject(
                text = "Logout",
                bgcolor = red,
                textcolor = black,
                width = 500.dp,
                height = 60.dp,
                onClick = {
                    coroutineScope.launch {
                        userViewModel.logOutUsers()
                        navigateWithLoading(Screen.Login.route)

                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
    }

    if (isLoading) {
        LoadingScreen()
    }

    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            navController.navigate(routeToNavigate) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateAge(dateOfBirthString: String): Int {
    // 1. Define the date format
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // 2. Parse the date of birth string to LocalDate
    val dateOfBirth: LocalDate = try {
        LocalDate.parse(dateOfBirthString, formatter)
    } catch (e: DateTimeParseException) {
        // Handle parsing error (e.g., invalid date format)
        return 0 // Or throw an exception, or return a default value
    }

    // 3. Get the current date
    val currentDate = LocalDate.now()

    // 4. Calculate the difference between the two dates using Period
    val period = Period.between(dateOfBirth, currentDate)

    // 5. Get the age in years
    return period.years
}

