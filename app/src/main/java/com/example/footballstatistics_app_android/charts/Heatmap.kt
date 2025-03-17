import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.Shader
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
import androidx.compose.ui.graphics.LinearGradient
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
import com.example.footballstatistics_app_android.Theme.green
import com.example.footballstatistics_app_android.Theme.red
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
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
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
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

    var isLoading by remember { mutableStateOf(true) }
    val match by matchViewModel.match.collectAsState()
    val locationDataList by locationViewModel.locations.collectAsState()

    LaunchedEffect(key1 = Unit) {
        Log.d("HeatmapChart", "Getting locations for match: $match_id")
        matchViewModel.getMatch(match_id)
    }

    var pitchMinLat by remember { mutableDoubleStateOf(0.00) }
    var pitchMinLon by remember { mutableDoubleStateOf(0.00) }
    var pitchMaxLat by remember { mutableDoubleStateOf(0.00) }
    var pitchMaxLon by remember { mutableDoubleStateOf(0.00) }

    LaunchedEffect(key1 = match) {
        if(match != null) {
            pitchMinLat = match?.home_corner_location?.split(",")?.get(0)?.toDouble()?.absoluteValue ?: 0.00
            pitchMinLon = match?.home_corner_location?.split(",")?.get(1)?.toDouble()?.absoluteValue ?: 0.00
            pitchMaxLat = match?.away_corner_location?.split(",")?.get(0)?.toDouble()?.absoluteValue ?: 0.00
            pitchMaxLon = match?.away_corner_location?.split(",")?.get(1)?.toDouble()?.absoluteValue ?: 0.00
            if(pitchMinLat > pitchMaxLat) {
                val auxLat = pitchMinLat
                pitchMinLat = pitchMaxLat
                pitchMaxLat = auxLat
            }
            if(pitchMinLon > pitchMaxLon) {
                val auxLon = pitchMinLon
                pitchMinLon = pitchMaxLon
                pitchMaxLon = auxLon
            }
            Log.d( "HeatmapChart", "Pitch locations set minLat: $pitchMinLat, maxLat: $pitchMaxLat, minLon: $pitchMinLon, maxLon: $pitchMaxLon")

            locationViewModel.getLocationsByMatchId(match_id.toString())
        }
    }

    LaunchedEffect(key1 = locationDataList) {
        Log.d("HeatmapChart", "Location data list size: ${locationDataList.size}")
        isLoading = false
    }

    if (isLoading) {
        Log.d("HeatmapChart", "Loading...")
        NoDataAvailable("Loading...")
    } else {
        if (locationDataList.isNotEmpty()) {
            Log.d("HeatmapChart", "Drawing heatmap for match: $match_id")
            CustomHeatmap(locationDataList, pitchMinLat, pitchMaxLat, pitchMinLon, pitchMaxLon, color)
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
                val latitude = location?.latitude?.toDouble()?.absoluteValue ?: 0.00
                val longitude = location?.longitude?.toDouble()?.absoluteValue ?: 0.00
                mapToPitch(
                    latitude,
                    longitude,
                    pitchMinLat,
                    pitchMinLon,
                    pitchMaxLat,
                    pitchMaxLon,
                    canvasWidth,
                    canvasHeight
                )
            }
            Log.d("CustomHeatmap", "Pitch points size: ${pitchPoints.size}")
            Log.d("CustomHeatmap", "Pitch points Set")
            val cellSize = size.width / 25
            val grid = mutableMapOf<Pair<Int, Int>, Int>()
            var maxDensity = 0
            pitchPoints.forEach { point ->
                // Calculate the correct grid X and Y for the canvas
                val gridX = (point.x / cellSize).toInt()
                val gridY = (point.y / cellSize).toInt()
                val density = grid.getOrDefault(Pair(gridX, gridY), 0) + 1

                Log.d("CustomHeatmap", "Point x: ${point.x}, Point y: ${point.y} , Grid X: $gridX, Grid Y: $gridY, Density: $density")

                grid[Pair(gridX, gridY)] = density
                maxDensity = if (density > maxDensity) density else maxDensity
            }
            Log.d("CustomHeatmap", "grid size: ${grid.size}")
            Log.d("CustomHeatmap", "Max density: $maxDensity")
            // Calculate the blur radius
            val calculatedBlurRadius = (cellSize / 10)
            val blurRadius = if (calculatedBlurRadius.isFinite() && calculatedBlurRadius >= 0.5f) {
                calculatedBlurRadius
            } else {
                Log.w("CustomHeatmap", "Invalid blur radius: $calculatedBlurRadius, using default radius.")
                5f // Use a default radius.
            }
            Log.d("CustomHeatmap", "Blur radius: $blurRadius")
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas

                val nativePaint = Paint().apply { // This is the correct Paint
                    maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                    isAntiAlias = true
                    alpha = 255
                }

                Log.d("CustomHeatmap", "Drawing heatmap")
                grid.forEach { (gridCoord) ->
                    val gridX = gridCoord.first
                    val gridY = gridCoord.second
                    val density = grid[gridCoord] ?: 0
                    nativePaint.color = color.toArgb()
                    if (maxDensity > 0) {
                        nativePaint.alpha = (255 * (density.toFloat() / maxDensity.toFloat())).toInt()
                    } else {
                        nativePaint.alpha = 1
                    }
                    if (nativePaint.alpha < 20) {
                        nativePaint.alpha = 20
                    }

                    Log.d("CustomHeatmap", "Drawing heatmap point: $gridX, $gridY, density: $density, alpha: ${nativePaint.alpha}")

                    val top = (gridY * cellSize)
                    val bottom = top + cellSize
                    val left = (gridX * cellSize )
                    val right = left + cellSize

                    nativeCanvas.drawRect(
                        left,
                        top,
                        right,
                        bottom,
                        nativePaint
                    )
                }
            }
            Log.d("CustomHeatmap", "Heatmap drawn")
        }

        Image(
            painter = painterResource(id = R.drawable.pitch_transparent),
            contentDescription = "Soccer Pitch Image",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

fun mapToPitch(
    latitude: Double, longitude: Double,
    pitchMinLat: Double, pitchMinLon: Double,
    pitchMaxLat: Double, pitchMaxLon: Double,
    canvasWidth: Float, canvasHeight: Float,
): Offset {
    Log.d("CustomHeatmap", "-------------------------------------------------")
    Log.d("CustomHeatmap", "Mapping location: ($latitude / $longitude). ")
    Log.d("CustomHeatmap", "To Pitch:  ($pitchMinLat / $pitchMinLon) - ($pitchMaxLat / $pitchMaxLon)")
    val clampedLat = latitude.coerceIn(pitchMinLat, pitchMaxLat)
    val clampedLon = longitude.coerceIn(pitchMinLon, pitchMaxLon)

    val latDifference = pitchMaxLat - pitchMinLat
    val lonDifference = pitchMaxLon - pitchMinLon

    if (latDifference <= 0.0 || lonDifference <= 0.0) {
        Log.w("CustomHeatmap", "Invalid pitch dimensions: latDifference=$latDifference, lonDifference=$lonDifference")
        return Offset(canvasWidth/2, canvasHeight/2)
    }

    val normalizedLat = (clampedLat - pitchMinLat) / latDifference
    val normalizedLon = (clampedLon - pitchMinLon) / lonDifference
    val invertedNormalizedLat = 1.0 - normalizedLat

    Log.d("CustomHeatmap", "To Normalized:  ($invertedNormalizedLat / $normalizedLon)")

    val pitchX = normalizedLon * canvasWidth
    val pitchY = invertedNormalizedLat * canvasHeight
    Log.d("CustomHeatmap", "To Canvas:  ($pitchX / $pitchY)")
    return Offset(pitchX.toFloat(), pitchY.toFloat())
}