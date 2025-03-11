import android.location.Location as AndroidLocation
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.Location
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.viewmodel.LocationViewModel
import com.example.footballstatistics_app_android.viewmodel.LocationViewModelFactory
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DistanceLineChart(match_id: String, color: Color = blue) {
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
            hasLocation = locationViewModel.checkIfMatchHasLocation(match_id)
            if (hasLocation) {
                locationViewModel.getLocationsByMatchId(match_id)
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
        val distanceData = calculateDistancePerMinute(locations)
        Log.d("DistanceLineChart", "distanceData: $distanceData")

        // Create the list of DataPoints
        val dataPoints = distanceData.map { (minute, distance) ->
            DataPoint(minute.toFloat(), distance.toFloat())
        }

        // LineChart from Compose Charts
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(264.dp),
            linePlot = LinePlot(
                listOf(dataPoints),
                lineColor = color,
            ),
        )
    }
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
            val minute = ((loc2!!.timestamp.toDouble() - firstTimestamp.toDouble()) / 1000 / 60).toDouble().roundToInt()
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