package com.example.footballstatistics_app_android.pages

import com.example.footballstatistics_app_android.charts.DistanceLineChart
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
import com.example.footballstatistics_app_android.charts.DirectionChart
import com.example.footballstatistics_app_android.components.ColorBar
import com.example.footballstatistics_app_android.components.StatBox
import com.example.footballstatistics_app_android.components.ViewTitle
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModelFactory
import com.example.footballstatistics_app_android.charts.DistanceBarChart
import com.example.footballstatistics_app_android.charts.HeatmapChart
import com.example.footballstatistics_app_android.charts.TimeInSideChart
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.viewmodel.LocationViewModel
import com.example.footballstatistics_app_android.viewmodel.LocationViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MatchPage(modifier: Modifier = Modifier, navController: NavController, match_id: Int) {
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
    val averageSpeed by locationViewModel.averageSpeed.collectAsState(initial = 0.0)
    val pitchSizeHorizontal by matchViewModel.pitchSizeHorizontal.collectAsState(initial = 0.0)
    val pitchSizeVertical by matchViewModel.pitchSizeVertical.collectAsState(initial = 0.0)

    LaunchedEffect(match_id) {
        Log.d("MatchPage", "Fetching match with ID: $match_id")
        coroutineScope.launch(Dispatchers.IO) {
            matchViewModel.getMatch(match_id)
            Log.d("MatchPage", "Fetched match: $match")
            locationViewModel.calculateTotalDistanceForMatch(match_id.toString())
            Log.d("MatchPage", "Calculated total distance: $totalDistance")
            locationViewModel.calculateTopSpeedForMatch(match_id.toString())
            Log.d("MatchPage", "Calculated top speed: $topSpeed")
            locationViewModel.calculateAverageSpeedForMatch(match_id.toString())
            Log.d("MatchPage", "Calculated average speed: $averageSpeed")
            matchViewModel.getMatchAndCalculatePitchSize(match_id)
            Log.d("MatchPage", "Calculated pitch size: $pitchSizeHorizontal x $pitchSizeVertical")
        }

    }

    val currentMatch = match ?: matchViewModel.emptyMatch()
    Log.d("MatchPage", "Current match: $currentMatch")


    var velocitySelected by remember { mutableStateOf(false) }
    var distanceSelected by remember { mutableStateOf(true) }
    var speedSelected by remember { mutableStateOf(false) }
    var chartName by remember { mutableStateOf("") }

    val chartTitleSize = 22.sp
    val titleSize = 50.sp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .verticalScroll(scrollState) ,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "MATCH DATA", image = R.drawable.match_img, navController = navController)
        Spacer(modifier = Modifier.height(24.dp))
        if (currentMatch.isExample) {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
            ) {
                ColorBar(
                    text = "EXAMPLE MATCH",
                    value = "",
                    bgcolor = yellow,
                    textcolor = black,
                    width = 500.dp,
                    height = 50.dp
                )
            }
        }
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
                modifier = Modifier.size(120.dp).padding(horizontal = 0.dp)
            )

        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)) {
            Text(
                text = "Pitch size: %.2fm x %.2fm ".format( pitchSizeHorizontal, pitchSizeVertical ),
                fontFamily = LeagueGothic,
                fontSize = 32.sp,
                color = white,
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
                                speedSelected = false
                                chartName = "TOP SPEED"
                    },
                    icon = R.drawable.speedometer,
                    text = "TOP SPEED",
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
                        speedSelected = false
                        chartName = "DISTANCE"
                    },
                    icon = R.drawable.sneaker,
                    text = "DISTANCE",
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
                        speedSelected = true
                        chartName = "AVERAGE SPEED"
                    },
                    icon = R.drawable.pace,
                    text = "AVERAGE SPEED",
                    value = "%.2f \n km/h".format(averageSpeed),
                    avg = "",
                    bgcolor = if(speedSelected) green else white,
                    textcolor = if(speedSelected) green else white,
                    height = 125.dp,
                    width = 100.dp,
                    selected = speedSelected
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "MATCH CHARTS",
            fontFamily = LeagueGothic,
            fontSize = titleSize,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "LOCATION HEATMAP",
            fontFamily = RobotoCondensed,
            fontSize = chartTitleSize,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column (Modifier.padding(horizontal = 36.dp )){
            HeatmapChart( match_id, green )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "POSITIONING OVER TIME BY SIDE",
            fontFamily = RobotoCondensed,
            fontSize = chartTitleSize,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column (Modifier.padding(horizontal = 36.dp )){
            TimeInSideChart(match_id, yellow, green)
        }


        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "DIRECTION MOVED ON MATCH TIME",
            fontFamily = RobotoCondensed,
            fontSize = chartTitleSize,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column (Modifier.padding(horizontal = 36.dp )){
            DirectionChart(match_id, yellow, green)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "TOTAL DISTANCE OVER TIME",
            fontFamily = RobotoCondensed,
            fontSize = chartTitleSize,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column (Modifier.padding(horizontal = 36.dp )){
            DistanceLineChart(match_id, blue)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "DISTANCE PER MINUTE",
            fontFamily = RobotoCondensed,
            fontSize = chartTitleSize,
            color = white,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column (Modifier.padding(horizontal = 36.dp )){
            DistanceBarChart(match_id, blue)
        }

        if (!currentMatch.isExample) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "MATCH SETTINGS",
                fontFamily = LeagueGothic,
                fontSize = titleSize,
                color = white,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
            ) {
                ButtonObject(
                    text = "DELETE MATCH",
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            matchViewModel.deleteMatch(currentMatch)
                            locationViewModel.deleteLocationsFromMatch(match_id.toString())
                            navController.navigate("home")
                        }
                    },
                    bgcolor = red,
                    textcolor = black,
                    width = 550.dp,
                    height = 60.dp
                )
            }

        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}