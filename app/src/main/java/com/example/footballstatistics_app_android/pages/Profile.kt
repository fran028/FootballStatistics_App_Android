package com.example.footballstatistics_app_android.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.example.footballstatistics_app_android.data.User
import com.example.footballstatistics_app_android.data.UserRepository
import com.example.footballstatistics_app_android.viewmodel.UserViewModel
import com.example.footballstatistics_app_android.viewmodel.UserViewModelFactory

//val LocalAppTheme = compositionLocalOf { AppTheme() }

@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController, updateSelectedItemIndex: (Int) -> Unit) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val appDatabase = AppDatabase.getDatabase(context)
    val userRepository = UserRepository(appDatabase.userDao())
    val viewModelFactory = UserViewModelFactory(userRepository)
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory)
    val coroutineScope = rememberCoroutineScope()
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
            text = "Franco Scarpettini ",
            fontFamily = RobotoCondensed,
            fontSize = 36.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "25 Years",
            fontFamily = RobotoCondensed,
            fontSize = 32.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(horizontal = 32.dp)) {
            Text(
                text = "175cm ",
                fontFamily = RobotoCondensed,
                fontSize = 24.sp,
                color = white
            )
            Text(
                text = "60Kg ",
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
                value = "10 MP",
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
                text = "HEARTRATE ",
                value = "159 bpm",
                bgcolor = red,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorBar(
                text = "TIME ",
                value = "10h 12m",
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
                RecordBox(
                    text = "MAX HEARTRATE ",
                    value = "190 bpm",
                    bgcolor = red,
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
                    //updateSelectedItemIndex(5)
                    val user: User? = userViewModel.getLoginUser()
                    if (user != null) {
                        userViewModel.updateLoginStatus(user.id)
                    }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

