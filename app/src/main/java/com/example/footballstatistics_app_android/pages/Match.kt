package com.example.footballstatistics_app_android.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Theme.LeagueGothic
import com.example.footballstatistics_app_android.Theme.RobotoCondensed
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.green
import com.example.footballstatistics_app_android.Theme.red
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.components.ColorBar
import com.example.footballstatistics_app_android.components.HeatmapChart
import com.example.footballstatistics_app_android.components.StatBox
import com.example.footballstatistics_app_android.components.ViewTitle
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.Match
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModelFactory
import androidx.compose.runtime.collectAsState
import com.example.footballstatistics_app_android.Screen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MatchPage(modifier: Modifier = Modifier, navController: NavController, match_id: String) {
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val matchRepository = MatchRepository(database.matchDao())
    val matchViewModelFactory = MatchViewModelFactory(matchRepository)
    val matchViewModel: MatchViewModel = viewModel(factory = matchViewModelFactory)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(match_id) {
        matchViewModel.getMatch(match_id)
    }

    val match by matchViewModel.match.collectAsState()

    if(match == null){
        navController.navigate(Screen.Home.route)
    }

    val currentMatch = match ?: matchViewModel.emptyMatch()


    var velocitySelected by remember { mutableStateOf(false) }
    var distanceSelected by remember { mutableStateOf(true) }
    var heartrateSelected by remember { mutableStateOf(false) }
    var chartName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .verticalScroll(scrollState) ,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "MATCH DATA", image = R.drawable.match_img)
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)) {
            Column {
                Text(
                    text = currentMatch.date,
                    fontFamily = RobotoCondensed,
                    fontSize = 40.sp,
                    color = white,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentMatch.total_time,
                    fontFamily = RobotoCondensed,
                    fontSize = 38.sp,
                    color = white,
                )
            }
            Image(
                painter = painterResource(id = R.drawable.pitch_color),
                contentDescription = "Soccer Image",
                modifier = Modifier.size(90.dp)
            )
        }


        Spacer(modifier = Modifier.height(20.dp))
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
            ) {
                StatBox(
                    onclick = { velocitySelected = true
                                distanceSelected = false
                                heartrateSelected = false
                                chartName = "VELOCITY"
                    },
                    icon = R.drawable.speedometer,
                    text = "VELOCITY",
                    value = "26 km/h",
                    avg = "Avg: 15 km/h",
                    bgcolor = if(velocitySelected) blue else white,
                    textcolor = if(velocitySelected) blue else white,
                    height = 125.dp,
                    width = 100.dp,
                    selected = velocitySelected
                )

                StatBox(
                    onclick = {
                        velocitySelected = false
                        distanceSelected = true
                        heartrateSelected = false
                        chartName = "DISTANCE"
                    },
                    icon = R.drawable.sneaker,
                    text = "DISTANCE ",
                    value = "7,3 km",
                    avg = "9:48m/km",
                    bgcolor = if(distanceSelected) yellow else white,
                    textcolor = if(distanceSelected) yellow else white,
                    height = 125.dp,
                    width = 100.dp,
                    selected = distanceSelected
                )
                StatBox(
                    onclick = {
                        velocitySelected = false
                        distanceSelected = false
                        heartrateSelected = true
                        chartName = "HEARTRATE"
                    },
                    icon = R.drawable.cardiogram,
                    text = "HEARTRATE ",
                    value = "190 bpm",
                    avg = "Avg: 169 bpm",
                    bgcolor = if(heartrateSelected) red else white,
                    textcolor = if(heartrateSelected) red else white,
                    height = 125.dp,
                    width = 100.dp,
                    selected = heartrateSelected
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "CHART - HEATMAP",
            fontFamily = RobotoCondensed,
            fontSize = 32.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column (Modifier.padding(horizontal = 36.dp )){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
                    .border(width = 4.dp, color = white, shape = RoundedCornerShape(8.dp))
                    .height(125.dp),
                contentAlignment = Alignment.Center
            ) {
                HeatmapChart( match_id )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "CHART - HEATMAP",
            fontFamily = RobotoCondensed,
            fontSize = 32.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column (Modifier.padding(horizontal = 36.dp )){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
                    .border(width = 4.dp, color = white, shape = RoundedCornerShape(8.dp))
                    .height(125.dp),
                contentAlignment = Alignment.Center
            ) {
                HeatmapChart( match_id )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "STATISTICS (Example)",
            fontFamily = LeagueGothic,
            fontSize = 32.sp,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)) {
            ColorBar(
                text = "DEFENSIVE ACTIONS",
                value = "30",
                bgcolor = yellow,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorBar(
                text = "ATTACKING ACTION",
                value = "27",
                bgcolor = green,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorBar(
                text = "JUMPS ",
                value = "10",
                bgcolor = blue,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorBar(
                text = "FALL/FOULS ",
                value = "5",
                bgcolor = red,
                textcolor = black,
                width = 500.dp,
                height = 50.dp
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}