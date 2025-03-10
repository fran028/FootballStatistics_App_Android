package com.example.footballstatistics_app_android.pages

import HeatmapChart
import android.os.Build
import android.util.Log
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
import com.example.footballstatistics_app_android.components.StatBox
import com.example.footballstatistics_app_android.components.ViewTitle
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.Match
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModelFactory
import androidx.compose.runtime.collectAsState
import com.example.footballstatistics_app_android.Screen
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.viewmodel.LocationViewModel
import com.example.footballstatistics_app_android.viewmodel.LocationViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    val locationRepository = LocationRepository(database.locationDao())
    val locationViewModelFactory = LocationViewModelFactory(locationRepository)
    val locationViewModel: LocationViewModel = viewModel(factory = locationViewModelFactory)

    val coroutineScope = rememberCoroutineScope()

    val match by matchViewModel.match.collectAsState(initial = null)
    val totalDistance by locationViewModel.totalDistance.collectAsState(initial = 0.0)
    val topSpeed by locationViewModel.topSpeed.collectAsState(initial = 0.0)
    val averagePace by locationViewModel.averagePace.collectAsState(initial = 0.0)
    LaunchedEffect(match_id) {
        Log.d("MatchPage", "Fetching match with ID: $match_id")
        coroutineScope.launch(Dispatchers.IO) {
            matchViewModel.getMatch(match_id)
            Log.d("MatchPage", "Fetched match: $match")
            locationViewModel.calculateTotalDistanceForMatch(match_id)
            Log.d("MatchPage", "Calculated total distance: $totalDistance")
            locationViewModel.calculateTopSpeedForMatch(match_id)
            Log.d("MatchPage", "Calculated top speed: $topSpeed")
            locationViewModel.calculateAveragePaceForMatch(match_id)
            Log.d("MatchPage", "Calculated average pace: $averagePace")
        }

    }

    val currentMatch = match ?: matchViewModel.emptyMatch()
    Log.d("MatchPage", "Current match: $currentMatch")


    var velocitySelected by remember { mutableStateOf(false) }
    var distanceSelected by remember { mutableStateOf(true) }
    var paceSelected by remember { mutableStateOf(false) }
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
                    fontFamily = LeagueGothic,
                    fontSize = 58.sp,
                    color = white,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentMatch.total_time,
                    fontFamily = LeagueGothic,
                    fontSize = 50.sp,
                    color = white,
                )
            }
            Image(
                painter = painterResource(id = R.drawable.pitch_color),
                contentDescription = "Soccer Image",
                modifier = Modifier.size(120.dp)
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
                                paceSelected = false
                                chartName = "VELOCITY"
                    },
                    icon = R.drawable.speedometer,
                    text = "VELOCITY",
                    value = "%.2f km/h".format(topSpeed),
                    avg = "",
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
                        paceSelected = false
                        chartName = "DISTANCE"
                    },
                    icon = R.drawable.sneaker,
                    text = "DISTANCE ",
                    value = "%.2f km".format(totalDistance),
                    avg = "",
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
                        paceSelected = true
                        chartName = "RUNNING PACE"
                    },
                    icon = R.drawable.pace,
                    text = "PACE ",
                    value = "%.2f km/h".format(averagePace),
                    avg = "",
                    bgcolor = if(paceSelected) green else white,
                    textcolor = if(paceSelected) green else white,
                    height = 125.dp,
                    width = 100.dp,
                    selected = paceSelected
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
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(Color.Transparent)
//                    .border(width = 4.dp, color = white, shape = RoundedCornerShape(8.dp))
//                    .height(125.dp),
//                contentAlignment = Alignment.Center
//            ) {
                HeatmapChart( match_id )
            //}
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
                //HeatmapChart( match_id )
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