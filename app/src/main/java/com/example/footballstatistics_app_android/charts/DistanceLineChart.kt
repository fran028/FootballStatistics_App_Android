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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
fun DistanceLineChart(match_id: Int, color: Color = blue) {
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
fun LineChartCompose(locations: List<Location?>, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(264.dp)
            .background(black)
    ) {
        val distanceData = calculateCumulativeDistancePerMinute(locations)
        Log.d("DistanceLineChart", "distanceData: $distanceData")
        DrawLineChart(distanceData, color)
    }
}

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
        val maxTime = distanceData.keys.maxOrNull() ?: 0

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

            //fill the path
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
            //Draw distance labels
            val distanceLabelCount = 5 // number of labels
            for (i in 0 until distanceLabelCount) {
                val yPos = height - (i.toFloat() / (distanceLabelCount - 1).toFloat()) * height
                val distanceValue = (maxDistance * (i.toFloat() / (distanceLabelCount - 1).toFloat())).roundToInt()
                val distanceValueInKilometers =roundToTwoDecimals(distanceValue / 1000.0)
                val textLayoutResult = textMeasurer.measure(
                    text = "$distanceValueInKilometers Km",
                    style = TextStyle(color = Color.White, fontSize = 10.sp)
                )
                drawText(
                    textLayoutResult,
                    topLeft = Offset(-textLayoutResult.size.width.toFloat() - 5.dp.toPx(), yPos - textLayoutResult.size.height / 2.toFloat())
                )
            }
            //Draw time labels
            val timeLabelCount = 5
            for (i in 0 until timeLabelCount) {
                val xPos = (i.toFloat() / (timeLabelCount - 1).toFloat()) * width
                val timeValue = (maxTime * (i.toFloat() / (timeLabelCount - 1).toFloat())).roundToInt()
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
fun roundToTwoDecimals(number: Double): Double {
    val formattedNumber = String.format("%.2f", number)
    return formattedNumber.toDouble()
}

fun calculateDistancePerMinute(locations: List<Location?>): Map<Int, Double> {
    val distancePerMinute = mutableMapOf<Int, Double>()
    if (locations.size < 2) {
        return distancePerMinute // Need at least two points to calculate distance
    }

    val sortedLocations = locations.sortedBy { it!!.timestamp }
    val firstTimestamp = sortedLocations.first()!!.timestamp
    sortedLocations.forEachIndexed { index, location ->
        if (index < sortedLocations.size - 1) {
            val loc2 = sortedLocations[index + 1]
            // Calculate the minute from the start of the match
            val minute = ((loc2!!.timestamp.toDouble() - firstTimestamp.toDouble()) / 1000 / 60).roundToInt()
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

            // Update the total distance for the minute
            distancePerMinute[minute] = distancePerMinute.getOrDefault(minute, 0.0) + distance
        }
    }
    return distancePerMinute
}

fun calculateCumulativeDistancePerMinute(locations: List<Location?>): Map<Int, Double> {
    val distancePerMinute = mutableMapOf<Int, Double>()
    if (locations.size < 2) {
        return distancePerMinute // Need at least two points to calculate distance
    }

    val sortedLocations = locations.sortedBy { it!!.timestamp }
    val firstTimestamp = sortedLocations.first()!!.timestamp
    var cumulativeDistance = 0.0

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