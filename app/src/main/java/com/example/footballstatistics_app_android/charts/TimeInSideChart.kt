package com.example.footballstatistics_app_android.charts

import android.util.Log
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import kotlin.math.absoluteValue
import androidx.compose.foundation.Canvas

// Gets the match id and searches for the locations of the match in the database.
// Once the data is obtained it calls the SideChart function
// That function transform the data and draws the heatmap on the screen
@Composable
fun TimeInSideChart(matchId: Int, colorLeft: Color = blue, colorRight: Color = yellow) {

    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()
    // Prepare to get the data from the database
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

    // Get match data and check if it has locations
    LaunchedEffect(key1 = Unit) {
        scope.launch(Dispatchers.IO) {
            Log.d("TimeChart", "Getting locations for match: $matchId")
            hasLocation = locationViewModel.checkIfMatchHasLocation(matchId.toString())
            if (hasLocation) {
                locationViewModel.getLocationsByMatchId(matchId.toString())
                Log.d("TimeChart", "Locations gotten for match: $matchId")
                matchViewModel.getMatch(matchId)
            } else {
                Log.d("TimeChart", "No locations found for match: $matchId")
            }
        }
        isLoading = false
    }

    if(isLoading){
        // Show loading indicator
        Log.d("TimeChart", "Loading...")
        NoDataAvailable("Loading...")
    } else {
        if (locationDataList.isNotEmpty() && match != null) {
            // Draw the Heatmap
            Log.d("TimeChart", "Drawing heatmap for match: $matchId")
            SideChart(locationDataList, match!!, colorLeft, colorRight)
        } else {
            // Notify that there is no data
            Log.d("TimeChart", "No available data for match: $matchId")
            NoDataAvailable("No available data")
        }
    }
}

// Show a text with the current state of the chart
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

// Creates the chart and draws it on the screen
@Composable
fun SideChart(locationDataList: List<Location?>, match: Match, colorLeft: Color, colorRight: Color){
    val height = 264.dp
    var leftSide = match.home_corner_location.split(",").get(1).toDouble().absoluteValue
    var righSide = match.away_corner_location.split(",").get(1).toDouble().absoluteValue
    if(leftSide > righSide){
        val aux = leftSide
        leftSide = righSide
        righSide = aux
    }

    val diff = righSide - leftSide
    Log.d("TimeChart", "Left side: $leftSide")
    Log.d("TimeChart", "Right side: $righSide")
    val halfPitch = leftSide + diff/2
    Log.d("TimeChart", "Half pitch: $halfPitch")
    var leftCount = 0
    var rightCount = 0
    val total = locationDataList.size
    locationDataList.forEach { location ->
        if (location != null) {
            val latitude = location.longitude.toDouble().absoluteValue
            if (latitude < halfPitch) {
               // Log.d("TimeChart", "Left Latitude: $latitude / Half pitch: $halfPitch")
                leftCount++
            } else {
                //Log.d("TimeChart", "Right Latitude: $latitude / Half pitch: $halfPitch")
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

    var fontcolorRight = white
    var fontcolorLeft = white
    if(averageLeft > 60){
        fontcolorLeft = black
    }
    if(averageRight > 60){
        fontcolorRight = black
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
                    .background(colorLeft.copy(alpha = 0.9f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rightSize.dp)
                    .background(colorRight.copy(alpha = 0.9f))
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
                color = fontcolorLeft
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding((height/4),0.dp,0.dp,0.dp),
                text = "${averageRight}%",
                fontFamily = LeagueGothic,
                fontSize = 40.sp,
                color = fontcolorRight
            )
        }
        Canvas(modifier = Modifier.matchParentSize()) {
            drawIntoCanvas { canvas ->
                // Draw pitch
                val lineThickness = 5.dp.toPx()
                val lineOffset = lineThickness / 2

                //left line
                drawLine(
                    color = white,
                    start = Offset(0f, 0f - lineOffset),
                    end = Offset(0f, size.height + lineOffset),
                    strokeWidth = lineThickness
                )
                //top line
                drawLine(
                    color = white,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = lineThickness
                )
                //Bottom Line
                drawLine(
                    color = white,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = lineThickness
                )
                //right line
                drawLine(
                    color = white,
                    start = Offset(size.width, 0f - lineOffset),
                    end = Offset(size.width, size.height + lineOffset),
                    strokeWidth = lineThickness
                )
                //Middle line
                drawLine(
                    color = white,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = lineThickness
                )
                //center circle
                drawCircle(
                    color = white,
                    radius = 100f,
                    center = Offset(size.width / 2, size.height / 2),
                    style = Stroke(width = lineThickness)
                )
                //left area
                drawRect(
                    color = white,
                    topLeft = Offset(0f, size.height / 4),
                    size = androidx.compose.ui.geometry.Size(
                        size.width / 10,
                        size.height - size.height / 2
                    ),
                    style = Stroke(width = lineThickness)
                )
                //right area
                drawRect(
                    color = white,
                    topLeft = Offset(size.width - size.width / 10, size.height / 4),
                    size = androidx.compose.ui.geometry.Size(
                        size.width / 10,
                        size.height - size.height / 2
                    ),
                    style = Stroke(width = lineThickness)
                )
            }
        }
    }
}