package com.example.footballstatistics_app_android.charts

import android.location.Location as AndroidLocation
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.Location
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.viewmodel.LocationViewModel
import com.example.footballstatistics_app_android.viewmodel.LocationViewModelFactory
//import com.madrapps.plot.line.DataPoint
//import com.madrapps.plot.line.LineGraph
//import com.madrapps.plot.line.LinePlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.io.path.moveTo
import kotlin.math.roundToInt
import kotlin.text.toFloat


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DistanceBarChart(match_id: Int, color: Color = blue, minuteInterval: Int = 5) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = AppDatabase.getDatabase(context)
    val locationRepository = LocationRepository(database.locationDao())
    val locationViewModelFactory = LocationViewModelFactory(locationRepository)
    val locationViewModel: LocationViewModel = viewModel(factory = locationViewModelFactory)

    val locationDataList by locationViewModel.locations.collectAsState(initial = emptyList())
    var hasLocation by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        scope.launch(Dispatchers.IO) {
            Log.d("DistanceLineChart", "Getting locations for match: $match_id")
            hasLocation = locationViewModel.checkIfMatchHasLocation(match_id.toString())
            if (hasLocation) {
                locationViewModel.getLocationsByMatchId(match_id.toString())
            } else {
                Log.d("DistanceLineChart", "No locations found for match: $match_id")
            }
        }
    }

    LaunchedEffect(key1 = locationDataList) {
        isLoading = false
    }

    if (isLoading) {
        Log.d("DistanceLineChart", "Loading...")
        NoDataAvailable("Loading...")
    } else {
        if (locationDataList.isNotEmpty()) {
            Log.d("DistanceLineChart", "Drawing linechart for match: $match_id")
            LineChartCompose(locationDataList, color)
        } else {
            Log.d("DistanceLineChart", "No available data for match: $match_id")
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
    ) {
        Text(
            text = text,
            color = white,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun LineChartCompose(locations: List<Location?>, color: Color, minuteInterval: Int = 5) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(264.dp)
            .background(black)
    ) {
        val distanceData = calculateDistancePer5Minutes(locations, minuteInterval)
        Log.d("DistanceLineChart", "distanceData: $distanceData")
        DrawBarChart(distanceData, color, minuteInterval)
    }
}

@Composable
fun DrawBarChart(distanceData: Map<Int, Double>, color: Color, minuteInterval: Int) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        val width = size.width
        val height = size.height
        val maxDistance = distanceData.values.maxOrNull() ?: 0.0
        val maxTime = distanceData.keys.maxOrNull() ?: 0

        val barCount = (maxTime / minuteInterval) + 1
        val barWidth = width / barCount.toFloat()

        val barSpacing = barWidth * 0.05f

        if (distanceData.isNotEmpty()) {

            // Draw bars
            for (i in 0 until barCount) {
                val time = i * minuteInterval
                val distance = distanceData.getOrDefault(time, 0.0)
                val barHeight = (distance.toFloat() / maxDistance.toFloat()) * height
                val left = (time.toFloat() / minuteInterval) * barWidth
                val top = height - barHeight
                drawRect(
                    color = color,
                    topLeft = Offset(left, top),
                    size = androidx.compose.ui.geometry.Size(barWidth - barSpacing, barHeight)
                )
            }

            // Draw x and y axis
            drawLine(
                color = Color.White,
                start = Offset(0f, 0f),
                end = Offset(0f, height),
                strokeWidth = 3.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 3.dp.toPx()
            )

            // Draw distance labels (y-axis)
            val distanceLabelCount = 5
            for (i in 0 until distanceLabelCount) {
                val yPos = height - (i.toFloat() / (distanceLabelCount - 1).toFloat()) * height
                val distanceValue =
                    (maxDistance * (i.toFloat() / (distanceLabelCount - 1).toFloat())).roundToInt()
                val textLayoutResult = textMeasurer.measure(
                    text = "$distanceValue m",
                    style = TextStyle(color = Color.White, fontSize = 10.sp)
                )
                drawText(
                    textLayoutResult,
                    topLeft = Offset(
                        -textLayoutResult.size.width.toFloat() - 5.dp.toPx(),
                        yPos - textLayoutResult.size.height / 2.toFloat()
                    )
                )
            }
            val timeLabelInterval = minuteInterval
            val firstLabel = distanceData.keys.minOrNull() ?: 0
            val lastLabel = distanceData.keys.maxOrNull() ?: 0

            val timeLabelCount = lastLabel/timeLabelInterval + 1
            for (i in 0 until timeLabelCount) {
                val time = i * timeLabelInterval

                if(time <= lastLabel){
                    val xPos = (time.toFloat() / timeLabelInterval) * barWidth + barWidth/2
                    val textLayoutResult = textMeasurer.measure(
                        text = "$time", // Show the minute
                        style = TextStyle(color = Color.White, fontSize = 10.sp)
                    )
                    drawText(
                        textLayoutResult,
                        topLeft = Offset(xPos - textLayoutResult.size.width / 2.toFloat(), height)
                    )
                }
            }
        }
    }
}

fun calculateDistancePer5Minutes(locations: List<Location?>, minuteInterval : Int = 5): Map<Int, Double> {
    val distancePerMinutes = mutableMapOf<Int, Double>()
    if (locations.size < 2) {
        return distancePerMinutes
    }

    val sortedLocations = locations.sortedBy { it!!.timestamp }
    val firstTimestamp = sortedLocations.first()!!.timestamp
    sortedLocations.forEachIndexed { index, location ->
        if (index < sortedLocations.size - 1) {
            val loc2 = sortedLocations[index + 1]
            val minute =
                ((loc2!!.timestamp.toDouble() - firstTimestamp.toDouble()) / 1000 / 60).roundToInt()
            // Calculate the 5-minute interval
            val minuteInterval = (minute / minuteInterval) * minuteInterval

            val result = FloatArray(1)
            AndroidLocation.distanceBetween(
                location!!.latitude.toDouble(),
                location.longitude.toDouble(),
                loc2.latitude.toDouble(),
                loc2.longitude.toDouble(),
                result
            )
            val distance = result[0].toDouble()

            // Update the total distance for the 5-minute interval
            distancePerMinutes[minuteInterval] =
                distancePerMinutes.getOrDefault(minuteInterval, 0.0) + distance
        }
    }
    return distancePerMinutes
}