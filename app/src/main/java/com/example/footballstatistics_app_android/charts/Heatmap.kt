import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.text.color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.Location
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.viewmodel.LocationViewModel
import com.example.footballstatistics_app_android.viewmodel.LocationViewModelFactory
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.text.first
import kotlin.text.forEach
import kotlin.text.split
import kotlin.text.toDouble
import kotlin.text.toFloat

@Composable
fun HeatmapChart(match_id: Int, color: Color = blue) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    val locationRepository = LocationRepository(database.locationDao())
    val locationViewModelFactory = LocationViewModelFactory(locationRepository)
    val locationViewModel: LocationViewModel = viewModel(factory = locationViewModelFactory)

    val matchRepository = MatchRepository(database.matchDao())
    val matchViewModelFactory = MatchViewModelFactory(matchRepository)
    val matchViewModel: MatchViewModel = viewModel(factory = matchViewModelFactory)

    // Pitch corners (you can adjust these as needed)
    val pitchMinLat = 51.75312
    val pitchMaxLat = 51.75328
    val pitchMinLon = -1.22835
    val pitchMaxLon = -1.22820

    // Pitch dimensions in meters (adjust these as needed)
    val pitchWidthInMeters = 105.0
    val pitchHeightInMeters = 68.0
    var isLoading by remember { mutableStateOf(true) }

    val locationDataList by locationViewModel.locations.collectAsState(initial = emptyList())
    var hasLocation by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        scope.launch(Dispatchers.IO) {
            Log.d("HeatmapChart", "Getting locations for match: $match_id")
            hasLocation = locationViewModel.checkIfMatchHasLocation(match_id.toString())
            if (hasLocation) {
                locationViewModel.getLocationsByMatchId(match_id.toString())
                Log.d("HeatmapChart", "Locations gotten for match: $match_id")
            } else {
                Log.d("HeatmapChart", "No locations found for match: $match_id")
            }
        }
    }

    LaunchedEffect(key1 = locationDataList) {
        Log.d("HeatmapChart", "Pitch locations set minLat: $pitchMinLat, maxLat: $pitchMaxLat, minLon: $pitchMinLon, maxLon: $pitchMaxLon")
        isLoading = false
    }

    if (isLoading) {
        Log.d("HeatmapChart", "Loading...")
        NoDataAvailable("Loading...")
    } else {
        if (locationDataList.isNotEmpty()) {
            Log.d("HeatmapChart", "Drawing heatmap for match: $match_id")
            CustomHeatmap(locationDataList, pitchMinLat, pitchMaxLat, pitchMinLon, pitchMaxLon, pitchWidthInMeters, pitchHeightInMeters, color)
        } else {
            Log.d("HeatmapChart", "No available data for match: $match_id")
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
fun CustomHeatmap(
    locationDataList: List<Location?>,
    pitchMinLat: Double,
    pitchMaxLat: Double,
    pitchMinLon: Double,
    pitchMaxLon: Double,
    pitchWidthInMeters: Double,
    pitchHeightInMeters: Double,
    color: Color,
) {
    Log.d("CustomHeatmap", "Drawing heatmap")
    Log.d("CustomHeatmap", "Location data size: ${locationDataList.size}")
    Log.d("CustomHeatmap", "MinLat: $pitchMinLat, MaxLat: $pitchMaxLat")
    Log.d("CustomHeatmap", "MinLon: $pitchMinLon, MaxLon: $pitchMaxLon")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(264.dp)
            .background(black)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            Log.d("CustomHeatmap", "Canvas dimensions: $canvasWidth x $canvasHeight")

            val pitchPoints = locationDataList.map { location ->
                val latitude = location?.latitude?.toDouble() ?: 0.00
                val longitude = location?.longitude?.toDouble() ?: 0.00
                mapToPitch(
                    latitude,
                    longitude,
                    pitchMinLat,
                    pitchMinLon,
                    pitchMaxLat,
                    pitchMaxLon,
                    pitchWidthInMeters,
                    pitchHeightInMeters
                )
            }
            Log.d("CustomHeatmap", "Pitch points size: ${pitchPoints.size}")
            Log.d("CustomHeatmap", "Pitch points Set")

            // Density Calculation
            val gridSize = 20.0 // Size of each grid cell in pitch units (e.g., 5 meters)
            val grid = mutableMapOf<Pair<Int, Int>, Int>()
            var maxDensity = 0
            pitchPoints.forEach { point ->
                // Calculate the correct grid X and Y for the canvas
                val gridX = (point.x / gridSize).toInt()
                val gridY = (point.y / gridSize).toInt()

                val density = grid.getOrDefault(Pair(gridX, gridY), 0) + 1
                grid[Pair(gridX, gridY)] = density
                maxDensity = if (density > maxDensity) density else maxDensity
            }
            Log.d("CustomHeatmap", "Pitch points size: ${pitchPoints.size}")
            Log.d("CustomHeatmap", "Max density: $maxDensity")
            // Calculate the blur radius
            val calculatedBlurRadius = (gridSize / 2).toFloat()
            Log.d("CustomHeatmap", "Calculated Blur radius: $calculatedBlurRadius")
            //Check if the calculated radius is valid.
            val blurRadius = if (calculatedBlurRadius.isFinite() && calculatedBlurRadius >= 0.5f) {
                calculatedBlurRadius
            } else {
                Log.w("CustomHeatmap", "Invalid blur radius: $calculatedBlurRadius, using default radius.")
                5f // Use a default radius.
            }
            Log.d("CustomHeatmap", "Blur radius: $blurRadius")
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                val nativePaint = androidx.compose.ui.graphics.Paint().apply {
                    maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                    isAntiAlias = true
                }
                Log.d("CustomHeatmap", "Drawing heatmap")
                // Define a scale ratio for the rectangles
                val scaleWidth = canvasWidth / pitchWidthInMeters
                val scaleHeight = canvasHeight / pitchHeightInMeters

                grid.forEach { (gridCoord) ->
                    val gridX = gridCoord.first
                    val gridY = gridCoord.second
                    val density = grid[gridCoord] ?: 0

                    nativePaint.color = color.toArgb()
                    if (maxDensity > 0) {
                        nativePaint.alpha = (255 * (density.toFloat() / maxDensity.toFloat())).toInt()
                    } else {
                        nativePaint.alpha = 0
                    }

                    val rectLeft = (gridX * gridSize * scaleWidth).toFloat()
                    val rectTop = (gridY * gridSize * scaleHeight).toFloat()
                    val rectRight = rectLeft + (gridSize * scaleWidth).toFloat()
                    val rectBottom = rectTop + (gridSize * scaleHeight).toFloat()

                    nativeCanvas.drawRect(
                        rectLeft,
                        rectTop,
                        rectRight,
                        rectBottom,
                        nativePaint
                    )
                }
            }
            Log.d("CustomHeatmap", "Heatmap drawn")
        }
        Image(
            painter = painterResource(id = R.drawable.pitch_transparent),
            contentDescription = "Soccer Pitch Image",
            modifier = Modifier.matchParentSize(), // Fills the parent (Box)
            contentScale = ContentScale.FillBounds // Scales to fill bounds
        )
    }
}

fun mapToPitch(
    latitude: Double, longitude: Double,
    pitchMinLat: Double, pitchMinLon: Double,
    pitchMaxLat: Double, pitchMaxLon: Double,
    pitchWidthInMeters: Double, pitchHeightInMeters: Double,
): Offset {

    // Calculate the difference between the max and min of latitude and longitude
    val latDifference = pitchMaxLat - pitchMinLat
    val lonDifference = pitchMaxLon - pitchMinLon

    // Calculate the normalized position of the longitude and latitude
    val normalizedLon = (longitude - pitchMinLon) / lonDifference
    val normalizedLat = (latitude - pitchMinLat) / latDifference

    // Scale the normalized values to the pitch dimensions
    val pitchX = normalizedLon * pitchWidthInMeters
    val pitchY = normalizedLat * pitchHeightInMeters

    // Ensure the y-axis is inverted for canvas mapping.
    val invertedPitchY = pitchHeightInMeters - pitchY

    return Offset(pitchX.toFloat(), invertedPitchY.toFloat())
}