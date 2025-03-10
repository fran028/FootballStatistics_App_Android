package com.example.footballstatistics_app_android.pages

import android.os.Build
import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.footballstatistics_app_android.components.ButtonIconObject
import com.example.footballstatistics_app_android.components.HeatmapChart
import com.example.footballstatistics_app_android.components.ViewTitle
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.Match
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.data.UserRepository
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModelFactory
import com.example.footballstatistics_app_android.viewmodel.UserViewModel
import com.example.footballstatistics_app_android.viewmodel.UserViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        matchViewModel.getLastMatches()
    }
    val lastMatches by matchViewModel.lastMatch.collectAsState()

    var lastMatch = matchViewModel.emptyMatch()

    if (!lastMatches.isEmpty()) {
        lastMatch = lastMatches[0] ?: matchViewModel.emptyMatch()
    }

    val newUser = if(lastMatches.isNotEmpty()) false else true



    var hasCreatedMatch by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = userViewModel.loginUser) {
        Log.d("HomePage", "Add match for user: ${userViewModel.loginUser}")
        if (userViewModel.loginUser != null && !hasCreatedMatch) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    matchViewModel.getMatchCount(userViewModel.loginUser.toString())
                    if (matchViewModel.matchCount.value == 0) {
                        Log.d(
                            "HomePage",
                            "No matches found for user: ${userViewModel.loginUser}"
                        )
                        // Add an example match for the logged-in user
                        val exampleMatch = Match(
                            id = "0",
                            user_id = userViewModel.loginUser.toString(),
                            date = LocalDate.now()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            ini_time = "00:00",
                            end_time = "00:00",
                            total_time = "60:00",
                            away_corner_location = "0",
                            home_corner_location = "0",
                            kickoff_location = "0"
                        )
                        Log.d( "HomePage", "Adding example match for user: ${userViewModel.loginUser}" )
                        matchViewModel.insertMatch(exampleMatch)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                hasCreatedMatch = true
                Log.d("HomePage", "Match added for user: ${userViewModel.loginUser}")
            }
        }
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "FULBO STATS", R.drawable.home_img)
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
            Text(
                text = "LAST MATCH CHART",
                fontFamily = LeagueGothic,
                fontSize = 40.sp,
                color = white
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (newUser) {
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
                    text = "HEATMAP",
                    fontFamily = LeagueGothic,
                    fontSize = 32.sp,
                    color = white,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                //HeatmapChart(lastMatch.id)
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
            if(lastMatches.isEmpty()){
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
                for (match in lastMatches){
                    ButtonIconObject(
                        text = "Match ${match?.id}",
                        onClick = { navController.navigate(Screen.Match.route) },
                        bgcolor = yellow,
                        height = 50.dp,
                        textcolor = black,
                        icon = R.drawable.soccer,
                        value = "${match?.date}"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}