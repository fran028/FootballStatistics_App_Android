package com.example.footballstatistics_app_android.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.charts.HeatmapChart
import com.example.footballstatistics_app_android.components.ButtonIconObject
import com.example.footballstatistics_app_android.components.ViewTitle
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.data.UserRepository
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModelFactory
import com.example.footballstatistics_app_android.viewmodel.UserViewModel
import com.example.footballstatistics_app_android.viewmodel.UserViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController) {

    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val matchRepository = MatchRepository(database.matchDao())
    val matchViewModelFactory = MatchViewModelFactory(matchRepository)
    val matchViewModel: MatchViewModel = viewModel(factory = matchViewModelFactory)
    val userRepository = UserRepository(database.userDao())
    val userViewModelFactory = UserViewModelFactory(userRepository)
    val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)

    val lastMatches by matchViewModel.lastMatch.collectAsState(initial = null)
    userViewModel.getLoginUser()

    var lastMatch = matchViewModel.emptyMatch()
    if (lastMatches != null && lastMatches!!.isNotEmpty()) {
        lastMatch = lastMatches!![0] ?: matchViewModel.emptyMatch()
    }

    LaunchedEffect(key1 = userViewModel.loginUser) {
        val user = userViewModel.loginUser.value
        if (user != null) {
            matchViewModel.getLastMatches(user.id.toString())
        }
    }

    LaunchedEffect(key1 = matchViewModel.lastMatch) {
        if (matchViewModel.lastMatch.value.isNotEmpty()) {
            lastMatch = lastMatches!![0] ?: matchViewModel.emptyMatch()
        }
    }

    val scrollState = rememberScrollState()

    var noMatches = true
    if(lastMatches != null) {
        if (lastMatches!!.isNotEmpty()) {
            noMatches = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "FULBO STATS", R.drawable.home_img, navController)
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)) {
            Column {
                Text(
                    text = "LAST MATCH PLAYED",
                    fontFamily = LeagueGothic,
                    fontSize = 40.sp,
                    color = white
                )
                if(lastMatch.isExample) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "EXAMPLE MATCH",
                        fontFamily = RobotoCondensed,
                        fontSize = 16.sp,
                        color = yellow
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lastMatch.date,
                    fontFamily = RobotoCondensed,
                    fontSize = 32.sp,
                    color = white
                )
            }
            Image(
                painter = painterResource(id = R.drawable.soccer_white),
                contentDescription = "Soccer Image",
                modifier = Modifier.size(70.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)) {
            if(noMatches) {
                Text(
                    text = "EmptyExample",
                    fontFamily = LeagueGothic,
                    fontSize = 32.sp,
                    color = white
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pitch_color),
                        contentDescription = "Soccer Image",
                        modifier = Modifier
                            .size(250.dp)
                            .rotate(90f)
                            .padding(0.dp)
                    )
                }
            } else {
                Text(
                    text = "GAME CHART - HEATMAP",
                    fontFamily = LeagueGothic,
                    fontSize = 32.sp,
                    color = white,
                )
                Spacer(modifier = Modifier.height(4.dp))
                HeatmapChart(lastMatch.id, yellow)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "LAST PLAYED MATCHES",
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
        Column (Modifier.padding(horizontal = 30.dp )) {
            if (!noMatches){
                for (match in lastMatches!!) {
                    var matchName = "Match ${match?.id}"
                    if(match?.isExample == true){
                        matchName = "Example Match"
                    }
                    ButtonIconObject(
                        text = matchName,
                        onClick = { navController.navigate(Screen.Match.createRoute(match?.id)) },
                        bgcolor = yellow,
                        height = 50.dp,
                        textcolor = black,
                        icon = R.drawable.soccer,
                        value = "${match?.date}"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
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
            }
        }
    }
}