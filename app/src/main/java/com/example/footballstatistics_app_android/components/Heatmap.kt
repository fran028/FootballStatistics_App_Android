package com.example.footballstatistics_app_android.components

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.Location
import com.example.footballstatistics_app_android.data.Match
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun HeatmapChart(match_id: String) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()
    var locationDataList by remember { mutableStateOf<List<Location>>(emptyList()) }
    var topLeftLat by remember { mutableDoubleStateOf(Double.MIN_VALUE) }
    var topLeftLon by remember { mutableDoubleStateOf(Double.MIN_VALUE) }
    var bottomRightLat by remember { mutableDoubleStateOf(Double.MIN_VALUE) }
    var bottomRightLon by remember { mutableDoubleStateOf(Double.MIN_VALUE) }
    var middleLat by remember { mutableDoubleStateOf(Double.MIN_VALUE) }
    var middleLon by remember { mutableDoubleStateOf(Double.MIN_VALUE) }
    var matchData: Match? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(key1 = true) {
        scope.launch {
            if(database.locationDao().checkIfMatchHasLocation(match_id)) {
                locationDataList = database.locationDao().getLocationsByMatchId(match_id)!!
                matchData = database.matchDao().getMatchById(match_id)
                topLeftLat = matchData!!.away_corner_location.split(",")[0].toDouble()
                topLeftLon = matchData!!.away_corner_location.split(",")[1].toDouble()
                bottomRightLat = matchData!!.home_corner_location.split(",")[0].toDouble()
                bottomRightLon = matchData!!.home_corner_location.split(",")[1].toDouble()
                middleLat = matchData!!.kickoff_location .split(",")[0].toDouble()
                middleLon = matchData!!.kickoff_location .split(",")[1].toDouble()
                isLoading = false
            }
        }
    }
    if(isLoading){
        NoDataAvailable("Loading...")
    } else {
        if (locationDataList.isNotEmpty()) {
            CustomHeatmap(locationDataList, topLeftLon, topLeftLat, bottomRightLon, bottomRightLat, middleLat, middleLon)
        } else {
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
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun CustomHeatmap(
    locationDataList: List<Location>,
    topLeftLat: Double,
    topLeftLon: Double,
    bottomRightLat: Double,
    bottomRightLon: Double,
    middleLat: Double,
    middleLon: Double
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Calculate pitch dimensions
            val pitchDimensions = calculatePitchDimensions(topLeftLat, topLeftLon, bottomRightLat, bottomRightLon, middleLat, middleLon)
            val pitchWidth = pitchDimensions.first
            val pitchHeight = pitchDimensions.second

            val pitchPoints = locationDataList.map { location ->
                mapGeoToPitch(
                    location.latitude,
                    location.longitude,
                    topLeftLon,
                    topLeftLat,
                    bottomRightLat,
                    bottomRightLon,
                    middleLat,
                    middleLon,
                    pitchWidth,
                    pitchHeight
                )
            }

            // 2. Density Calculation
            val gridSize = 5 // Size of each grid cell in pitch units (e.g., 5 meters)
            val grid = mutableMapOf<Pair<Int, Int>, Int>()
            var maxDensity = 0

            pitchPoints.forEach { point ->
                val gridX = (point.x / gridSize).toInt()
                val gridY = (point.y / gridSize).toInt()
                val density = grid.getOrDefault(Pair(gridX, gridY), 0) + 1
                grid[Pair(gridX, gridY)] = density
                maxDensity = if (density > maxDensity) density else maxDensity
            }

            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                val nativePaint = Paint().apply {
                    maskFilter = BlurMaskFilter(gridSize.toFloat(), BlurMaskFilter.Blur.NORMAL)
                    isAntiAlias = true
                }

                grid.forEach { (gridCoord) ->
                    val gridX = gridCoord.first
                    val gridY = gridCoord.second
                    val color = blue

                    nativePaint.color = color.toArgb()
                    nativePaint.alpha = (maxDensity.toFloat()).toInt()

                    val rectLeft = (gridX * gridSize * (canvasWidth / pitchWidth)).toFloat()
                    val rectTop = (gridY * gridSize * (canvasHeight / pitchHeight)).toFloat()
                    val rectRight = rectLeft + (gridSize * (canvasWidth / pitchWidth)).toFloat()
                    val rectBottom = rectTop + (gridSize * (canvasHeight / pitchHeight)).toFloat()

                    nativeCanvas.drawRect(
                        rectLeft,
                        rectTop,
                        rectRight,
                        rectBottom,
                        nativePaint
                    )
                }
            }
        }
    }
}

fun mapGeoToPitch(
    latitude: String,
    longitude: String,
    topLeftLat: Double,
    topLeftLon: Double,
    bottomRightLat: Double,
    bottomRightLon: Double,
    middleLat: Double,
    middleLon: Double,
    pitchWidth: Double,
    pitchHeight: Double
): Offset {

    // Calculate differences in latitude and longitude
    val latDiff = topLeftLat - bottomRightLat
    val lonDiff = bottomRightLon - topLeftLon

    // Calculate the offset from the top-left corner
    val latOffset = latitude.toDouble() - topLeftLat
    val lonOffset = longitude.toDouble() - topLeftLon

    // Map the offsets to pitch coordinates
    val pitchX = (lonOffset / lonDiff) * pitchWidth
    val pitchY = (latOffset / latDiff) * pitchHeight

    return Offset(pitchX.toFloat(), pitchY.toFloat())
}

fun calculatePitchDimensions(topLeftLat: Double, topLeftLon: Double, bottomRightLat: Double, bottomRightLon: Double, middleLat: Double, middleLon: Double): Pair<Double, Double> {

    // Calculate distances using the Haversine formula
    val diagonalDistance = haversine(topLeftLat, topLeftLon, bottomRightLat, bottomRightLon)
    val topLeftToMiddleDistance = haversine(topLeftLat, topLeftLon, middleLat, middleLon)
    val middleToBottomRightDistance = haversine(middleLat, middleLon, bottomRightLat, bottomRightLon)

    // Approximate pitch dimensions (assuming a rectangular pitch)
    // Using the Pythagorean theorem and the distances to estimate width and height
    val pitchWidth = (topLeftToMiddleDistance + middleToBottomRightDistance) / 2
    val pitchHeight = sqrt(diagonalDistance * diagonalDistance - pitchWidth * pitchWidth)

    return Pair(pitchWidth, pitchHeight)
}

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Radius of the Earth in kilometers

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return r * c
}