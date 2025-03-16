package com.example.footballstatistics_app_android.charts

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Theme.LeagueGothic
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.Location
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.data.Match
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.viewmodel.LocationViewModel
import com.example.footballstatistics_app_android.viewmodel.LocationViewModelFactory
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TimeInSideChart(match_id: Int, color_left: Color = blue, color_right: Color = yellow) {

    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    val locationRepository = LocationRepository(database.locationDao())
    val locationViewModelFactory = LocationViewModelFactory(locationRepository)
    val locationViewModel: LocationViewModel = viewModel(factory = locationViewModelFactory)

    val matchRepository = MatchRepository(database.matchDao())
    val matchViewModelFactory = MatchViewModelFactory(matchRepository)
    val matchViewModel: MatchViewModel = viewModel(factory = matchViewModelFactory)

    var isLoading by remember { mutableStateOf(true) }

    val match by matchViewModel.match.collectAsState(initial = null)
    val locationDataList by locationViewModel.locations.collectAsState(initial = emptyList())
    var hasLocation by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        scope.launch(Dispatchers.IO) {
            Log.d("TimeChart", "Getting locations for match: $match_id")
            hasLocation = locationViewModel.checkIfMatchHasLocation(match_id.toString())
            if (hasLocation) {
                locationViewModel.getLocationsByMatchId(match_id.toString())
                Log.d("TimeChart", "Locations gotten for match: $match_id")
                matchViewModel.getMatch(match_id)
            } else {
                Log.d("TimeChart", "No locations found for match: $match_id")
            }
        }
        isLoading = false
    }

    if(isLoading){
        Log.d("TimeChart", "Loading...")
        NoDataAvailable("Loading...")
    } else {
        if (locationDataList.isNotEmpty() && match != null) {
            Log.d("TimeChart", "Drawing heatmap for match: $match_id")
            SideChart(locationDataList, match!!, color_left, color_right)
        } else {
            Log.d("TimeChart", "No available data for match: $match_id")
            NoDataAvailable("No available data")
        }
    }
}

@Composable
private fun NoDataAvailable(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
    ){
        Text(
            text = text,
            color = white,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun SideChart(locationDataList: List<Location?>, match: Match, color_left: Color, color_right: Color){
    val height = 264.dp
    val halfPitch = match.kickoff_location.split(",")[1].toDouble()
    Log.d("TimeChart", "Half pitch: $halfPitch")
    var leftCount = 0
    var rightCount = 0
    val total = locationDataList.size
    locationDataList.forEach { location ->
        if (location != null) {
            val latitude = location.longitude.toDouble()
            if (latitude < halfPitch) {
                leftCount++
            } else {
                rightCount++
            }
        }
    }

    val leftSize = (leftCount.toFloat() / total.toFloat()) * height.value
    val rightSize = (rightCount.toFloat() / total.toFloat()) * height.value
    Log.d("TimeChart", "Left size: $leftSize")
    Log.d("TimeChart", "Right size: $rightSize")
    Log.d("TimeChart", "Total: $total")
    Log.d("TimeChart", "Left count: $leftCount")
    Log.d("TimeChart", "Right count: $rightCount")

    val averageLeft = leftCount * 100 / total
    val averageRight = rightCount * 100 / total
    Log.d("TimeChart", "Average left: $averageLeft")
    Log.d("TimeChart", "Average right: $averageRight")

    var fontColor_right = white
    var fontColor_left = white
    if(averageLeft > 60){
        fontColor_left = black
    }
    if(averageRight > 60){
        fontColor_right = black
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(black),
    ) {
        Row (
            modifier = Modifier.matchParentSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(leftSize.dp)
                    .background(color_left)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rightSize.dp)
                    .background(color_right)
            )
        }
        Row (
            modifier = Modifier.matchParentSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(0.5f).padding((height/4),0.dp,0.dp,0.dp),
                text = "${averageLeft}%",
                fontFamily = LeagueGothic,
                fontSize = 40.sp,
                color = fontColor_left
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding((height/4),0.dp,0.dp,0.dp),
                text = "${averageRight}%",
                fontFamily = LeagueGothic,
                fontSize = 40.sp,
                color = fontColor_right
            )
        }

        Image(
            painter = painterResource(id = R.drawable.pitch_transparent),
            contentDescription = "Soccer Pitch Image",
            modifier = Modifier.matchParentSize(), // Fills the parent (Box)
            contentScale = ContentScale.FillBounds // Scales to fill bounds
        )
    }
}