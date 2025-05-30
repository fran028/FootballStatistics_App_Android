package com.example.footballstatistics_app_android.charts

import android.location.Location as AndroidLocation
import android.util.Log
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DistanceLineChart(matchId: Int, color: Color = blue) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Get Data from Database
    val database = AppDatabase.getDatabase(context)
    val locationRepository = LocationRepository(database.locationDao())
    val locationViewModelFactory = LocationViewModelFactory(locationRepository)
    val locationViewModel: LocationViewModel = viewModel(factory = locationViewModelFactory)

    val locationDataList by locationViewModel.locations.collectAsState(initial = emptyList())
    var hasLocation by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Check if there is a location for the match
    LaunchedEffect(key1 = Unit) {
        scope.launch(Dispatchers.IO) {
            Log.d("com.example.footballstatistics_app_android.charts.DistanceLineChart", "Getting locations for match: $matchId")
            hasLocation = locationViewModel.checkIfMatchHasLocation(matchId.toString())
            if (hasLocation) {
                locationViewModel.getLocationsByMatchId(matchId.toString())
            } else {
                Log.d("com.example.footballstatistics_app_android.charts.DistanceLineChart", "No locations found for match: $matchId")
            }
        }
    }

    // Once we have the locations, draw the chart
    LaunchedEffect(key1 = locationDataList) {
        isLoading = false
    }

    if (isLoading) {
        // Show Loading message
        Log.d("com.example.footballstatistics_app_android.charts.DistanceLineChart", "Loading...")
        NoDataAvailable("Loading...")
    } else {
        if (locationDataList.isNotEmpty()) {
            // Draw line chart
            Log.d("com.example.footballstatistics_app_android.charts.DistanceLineChart", "Drawing linechart for match: $matchId")
            LineChartCompose(locationDataList, color)
        } else {
            // Show no data available message
            Log.d("com.example.footballstatistics_app_android.charts.DistanceLineChart", "No available data for match: $matchId")
            NoDataAvailable("No available data")
        }
    }
}

// Show text that inform the user about the possible states of the chart
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

// Transform the data to fit the chart
// Call the DrawLineChart function and sets it in a compose element
@Composable
fun LineChartCompose(locations: List<Location?>, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(264.dp)
            .background(black)
    ) {
        val distanceData = calculateCumulativeDistancePerMinute(locations)
        Log.d("com.example.footballstatistics_app_android.charts.DistanceLineChart", "distanceData: $distanceData")
        DrawLineChart(distanceData, color)
    }
}

// Gets the transformed data as a parameter
// And draws it in a canvas
@Composable
fun DrawLineChart(distanceData: Map<Int, Double>, color: Color) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
    ){
        val width = size.width
        val height = size.height
        // Find the maximum distance and time
        val maxDistance = distanceData.values.maxOrNull() ?: 0.0
        val maxTime = (distanceData.keys.maxOrNull() ?: 0)

        // Create a path for the line chart
        val linePath = Path()
        val fillPath = Path() // path for the space below the line

        // Calculate a list with the points of the chart
        val points = distanceData.map { (time, distance) ->
            // Calculate the position of the point in the canvas
            val x = if (maxTime == 0) 0f else (time.toFloat() / maxTime.toFloat()) * width
            val y = if (maxDistance == 0.0) height else height - (distance.toFloat() / maxDistance.toFloat()) * height
            Offset(x, y)
        }
        if (points.isNotEmpty()) {
            fillPath.moveTo(0f, height)
            linePath.moveTo(points.first().x, points.first().y)
            fillPath.lineTo(points.first().x, points.first().y)
            points.forEach {
                linePath.lineTo(it.x, it.y)
                fillPath.lineTo(it.x, it.y)
            }
            fillPath.lineTo(width, height)
            fillPath.close()

            // Fill the path
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                ),
                style = Fill
            )
            // Draw the line chart
            drawPath(
                path = linePath,
                color = color,
                style = Stroke(width = 3.dp.toPx())
            )

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

            //Draw distance labels (y-axis)
            val distanceLabelCount = 5 // number of labels
            for (i in 0 until distanceLabelCount) {
                val yPos = height - (i.toFloat() / (distanceLabelCount - 1).toFloat()) * height
                val distanceValue = (maxDistance * (i.toFloat() / (distanceLabelCount - 1).toFloat())).roundToInt()
                val distanceValueInKilometers = roundToTwoDecimals(distanceValue / 1000.0)
                val textLayoutResult = textMeasurer.measure(
                    text = "$distanceValueInKilometers Km",
                    style = TextStyle(color = Color.White, fontSize = 10.sp)
                )
                drawText(
                    textLayoutResult,
                    topLeft = Offset(-textLayoutResult.size.width.toFloat() - 5.dp.toPx(), yPos - textLayoutResult.size.height / 2.toFloat())
                )
            }

            //Draw time labels (x-axis)
            val timeLabelCount = 5
            for (i in 0 until timeLabelCount) {
                val xPos = (i.toFloat() / (timeLabelCount - 1).toFloat()) * width
                val timeValue = (maxTime/2 * (i.toFloat() / (timeLabelCount - 1).toFloat())).roundToInt()
                val textLayoutResult = textMeasurer.measure(
                    text = "$timeValue min",
                    style = TextStyle(color = Color.White, fontSize = 10.sp)
                )
                drawText(
                    textLayoutResult,
                    topLeft = Offset(xPos - textLayoutResult.size.width / 2.toFloat(), height+5)
                )
            }
        }
    }
}

private fun roundToTwoDecimals(number: Double): Double {
    val formattedNumber = String.format("%.2f", number)
    return formattedNumber.toDouble()
}

// Calculates the total distance between every consecutive location
// Returns a map with the time as key and the distance as value
fun calculateCumulativeDistancePerMinute(locations: List<Location?>): Map<Int, Double> {
    val distancePerMinute = mutableMapOf<Int, Double>()
    if (locations.size < 2) {
        return distancePerMinute // Need at least two points to calculate distance
    }

    // Sort the locations by timestamp
    val sortedLocations = locations.sortedBy { it!!.timestamp }
    val firstTimestamp = sortedLocations.first()!!.timestamp
    var cumulativeDistance = 0.0

    // Cycle through the locations
    // Calculate the distance between each consecutive location
    sortedLocations.forEachIndexed { index, location ->
        if (index < sortedLocations.size - 1) {
            val loc2 = sortedLocations[index + 1]
            // Calculate the minute from the start of the match
            val minute =
                ((loc2!!.timestamp.toDouble() - firstTimestamp.toDouble()) / 1000 / 60).roundToInt()
            // Calculate distance
            val result = FloatArray(1)
            AndroidLocation.distanceBetween(
                location!!.latitude.toDouble(),
                location.longitude.toDouble(),
                loc2.latitude.toDouble(),
                loc2.longitude.toDouble(),
                result
            )
            val distance = result[0].toDouble()

            // Update the cumulative distance
            cumulativeDistance += distance
            distancePerMinute[minute] = cumulativeDistance
        }
    }
    return distancePerMinute
}