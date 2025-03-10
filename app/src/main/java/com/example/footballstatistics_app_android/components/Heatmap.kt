import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import kotlin.text.split
import kotlin.text.toDouble

@Composable
fun HeatmapChart(match_id: String, color: Color = blue) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    val locationRepository = LocationRepository(database.locationDao())
    val locationViewModelFactory = LocationViewModelFactory(locationRepository)
    val locationViewModel: LocationViewModel = viewModel(factory = locationViewModelFactory)

    val matchRepository = MatchRepository(database.matchDao())
    val matchViewModelFactory = MatchViewModelFactory(matchRepository)
    val matchViewModel: MatchViewModel = viewModel(factory = matchViewModelFactory)

    var minLat by remember { mutableDoubleStateOf(Double.MAX_VALUE) }
    var maxLat by remember { mutableDoubleStateOf(Double.MIN_VALUE) }
    var minLon by remember { mutableDoubleStateOf(Double.MAX_VALUE) }
    var maxLon by remember { mutableDoubleStateOf(Double.MIN_VALUE) }
    var isLoading by remember { mutableStateOf(true) }

    val match by matchViewModel.match.collectAsState(initial = null)
    val locationDataList by locationViewModel.locations.collectAsState(initial = emptyList())
    var hasLocation by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        scope.launch(Dispatchers.IO) {
            Log.d("HeatmapChart", "Getting locations for match: $match_id")
            hasLocation = locationViewModel.checkIfMatchHasLocation(match_id)
            if (hasLocation) {
                locationViewModel.getLocationsByMatchId(match_id)
                Log.d("HeatmapChart", "Locations gotten for match: $match_id")
                matchViewModel.getMatch(match_id)
            } else {
                Log.d("HeatmapChart", "No locations found for match: $match_id")
            }
        }
    }

    LaunchedEffect(key1 = locationDataList, key2 = match) {
        if (match != null) {
            minLat = match!!.away_corner_location.split(",")[0].toDouble()
            maxLat = match!!.home_corner_location.split(",")[0].toDouble()
            minLon = match!!.home_corner_location.split(",")[1].toDouble()
            maxLon = match!!.away_corner_location.split(",")[1].toDouble()
        } else {
            if(locationDataList.isNotEmpty()) {
                locationDataList.forEach { location ->
                    if (location != null) {
                        val lat = location.latitude.toDouble()
                        val lon = location.longitude.toDouble()
                        minLat = minOf(minLat, lat)
                        maxLat = maxOf(maxLat, lat)
                        minLon = minOf(minLon, lon)
                        maxLon = maxOf(maxLon, lon)
                    }
                }
            }
        }
        Log.d("HeatmapChart", "Pitch locations set minLat: $minLat, maxLat: $maxLat, minLon: $minLon, maxLon: $maxLon")
        isLoading = false
    }

    if(isLoading){
        Log.d("HeatmapChart", "Loading...")
        NoDataAvailable("Loading...")
    } else {
        if (locationDataList.isNotEmpty()) {
            Log.d("HeatmapChart", "Drawing heatmap for match: $match_id")
            CustomHeatmap(locationDataList, minLat, maxLat, minLon, maxLon, color)
        } else {
            Log.d("HeatmapChart", "No available data for match: $match_id")
            NoDataAvailable("No available data")
        }
    }
}

@Composable
fun NoDataAvailable(text: String) {
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
fun CustomHeatmap(
    locationDataList: List<Location?>,
    minLat: Double,
    maxLat: Double,
    minLon: Double,
    maxLon: Double,
    color: Color
) {
    Log.d("CustomHeatmap", "Drawing heatmap")
    Log.d("CustomHeatmap", "Location data size: ${locationDataList.size}")
    Log.d("CustomHeatmap", "MinLat: $minLat, MaxLat: $maxLat")
    Log.d("CustomHeatmap", "MinLon: $minLon, MaxLon: $maxLon")
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
            // Define pitch dimensions using max and min values
            val pitchWidth = maxLon - minLon
            val pitchHeight = maxLat - minLat

            Log.d("CustomHeatmap", "Pitch dimensions: $pitchWidth x $pitchHeight")

            val pitchPoints = locationDataList.map { location ->
                val latitude = location?.latitude?.toDouble() ?: 0.00
                val longitude = location?.longitude?.toDouble() ?: 0.00
                mapToPitch(
                    latitude,
                    longitude,
                    minLon,
                    minLat,
                    pitchWidth,
                    pitchHeight
                )
            }
            Log.d("CustomHeatmap", "Pitch points size: ${pitchPoints.size}")
            //pitchPoints.forEach { Log.d("CustomHeatmap", "Pitch point: ${it.x}, ${it.y}") }
            Log.d("CustomHeatmap", "Pitch points Set")

            val gridCellWidth = 20 // Set the grid cell width in pixels
            val gridCellHeight = 20 // Set the grid cell height in pixels

            // Density Calculation
            val gridSize = 20.0 // Size of each grid cell in pitch units (e.g., 5 meters)
            val grid = mutableMapOf<Pair<Int, Int>, Int>()
            var maxDensity = 0
            pitchPoints.forEach { point ->
                // Normalize point to the canvas size
                val normalizedX = point.x / pitchWidth
                val normalizedY = point.y / pitchHeight

                // Apply the correct canvas size
                val canvasX = normalizedX * canvasWidth
                val canvasY = canvasHeight - normalizedY * canvasHeight //Invert the Y axis correctly.

                // Calculate the correct grid X and Y for the canvas
                val gridX = (canvasX / gridSize).toInt()
                val gridY = (canvasY / gridSize).toInt()
                //Log.d("CustomHeatmap", "Point coordinates: $gridX, $gridY")

                val density = grid.getOrDefault(Pair(gridX, gridY), 0) + 1
                grid[Pair(gridX, gridY)] = density
                maxDensity = if (density > maxDensity) density else maxDensity
            }
            Log.d("CustomHeatmap", "Pitch points size: ${pitchPoints.size}")
            Log.d("CustomHeatmap", "Max density: $maxDensity")
            // Calculate the blur radius
            val calculatedBlurRadius = (gridSize/20).toFloat()
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
                val nativePaint = Paint().apply {
                    maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                    isAntiAlias = true
                }
                Log.d("CustomHeatmap", "Drawing heatmap")
                grid.forEach { (gridCoord) ->
                    val gridX = gridCoord.first
                    val gridY = gridCoord.second
                    val density = grid[gridCoord] ?: 0

                    nativePaint.color = color.toArgb()
                    nativePaint.alpha = (255 * (density.toFloat() / maxDensity.toFloat())).toInt()

                    val rectLeft = (gridX * gridSize).toFloat()
                    val rectTop = (gridY * gridSize).toFloat()
                    val rectRight = rectLeft + gridSize
                    val rectBottom = rectTop + gridSize

                    nativeCanvas.drawRect(
                        rectLeft,
                        rectTop,
                        rectRight.toFloat(),
                        rectBottom.toFloat(),
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
    minLon: Double, minLat: Double,
    pitchWidth: Double, pitchHeight: Double,
): Offset {
    // Normalize values to 0-1 range
    val normalizedLon = (longitude - minLon) / pitchWidth
    val normalizedLat = (latitude - minLat) / pitchHeight
    // Map to pitch dimensions
    val pitchX = normalizedLon * pitchWidth
    //Invert the Y axis
    val pitchY = pitchHeight - normalizedLat * pitchHeight

    return Offset(pitchX.toFloat(), pitchY.toFloat())
}