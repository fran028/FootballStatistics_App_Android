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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModelFactory
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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        matchViewModel.getLastMatches()
    }
    val lastMatches by matchViewModel.lastMatch.collectAsState()

    val lastMatch = lastMatches[0] ?: matchViewModel.emptyMatch()

    val newUser = if(lastMatches.isNotEmpty()) false else true

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
            Text(
                text = "LAST MATCH CHART",
                fontFamily = LeagueGothic,
                fontSize = 40.sp,
                color = white
            )
            Spacer(modifier = Modifier.height(16.dp))

        if(newUser) {
            Text(
                text = "EmptyExample",
                fontFamily = LeagueGothic,
                fontSize = 32.sp,
                color = white,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Column(Modifier.padding(horizontal = 36.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.pitch_color),
                    contentDescription = "Soccer Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(90f)
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
            Column(Modifier.padding(horizontal = 36.dp)) {
                HeatmapChart(lastMatch.id)
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