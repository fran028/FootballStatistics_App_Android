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
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.io.path.moveTo
import kotlin.math.atan2


val TAG = "DirectionChart"
// Gets the match id and searches for the locations of the match in the database.
// Once the data is obtained it calls the SideChart function
// That function transform the data and draws the heatmap on the screen
@Composable
fun DirectionChart(matchId: Int, colorLeft: Color = blue, colorRight: Color = yellow) {

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
            Log.d(TAG, "Getting locations for match: $matchId")
            hasLocation = locationViewModel.checkIfMatchHasLocation(matchId.toString())
            if (hasLocation) {
                locationViewModel.getLocationsByMatchId(matchId.toString())
                Log.d(TAG, "Locations gotten for match: $matchId")
                matchViewModel.getMatch(matchId)
            } else {
                Log.d(TAG, "No locations found for match: $matchId")
            }
        }
        isLoading = false
    }

    if(isLoading){
        // Show loading indicator
        Log.d(TAG, "Loading...")
        NoDataAvailable("Loading...")
    } else {
        if (locationDataList.isNotEmpty() && match != null) {
            // Draw the Heatmap
            Log.d(TAG, "Drawing heatmap for match: $matchId")
            BarChart(locationDataList, match!!, colorLeft, colorRight)
        } else {
            // Notify that there is no data
            Log.d(TAG, "No available data for match: $matchId")
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
fun BarChart(locationDataList: List<Location?>, match: Match, colorLeft: Color, colorRight: Color){
    val height = 264.dp
    val textMeasurer = rememberTextMeasurer()
    var leftSide = match.home_corner_location.split(",").get(1).toDouble().absoluteValue
    var righSide = match.away_corner_location.split(",").get(1).toDouble().absoluteValue
    if(leftSide > righSide){
        val aux = leftSide
        leftSide = righSide
        righSide = aux
    }

    val diff = righSide - leftSide
    Log.d(TAG, "Left side: $leftSide")
    Log.d(TAG, "Right side: $righSide")
    val halfPitch = leftSide + diff/2
    Log.d(TAG, "Half pitch: $halfPitch")
    var leftCount = 0
    var rightCount = 0
    val total = locationDataList.size

    for (i in 0 until total-1){
        val location = locationDataList[i]
        val nextLocation = locationDataList[i+1]

        //calculate the direction traveled from location 1 to 2
        val locationLatitude = location?.latitude?.toDouble()?.absoluteValue ?: 0.00
        val locationLongitude = location?.longitude?.toDouble()?.absoluteValue ?: 0.00
        val nextLocationLatitude = nextLocation?.latitude?.toDouble()?.absoluteValue ?: 0.00
        val nextLocationLongitude = nextLocation?.longitude?.toDouble()?.absoluteValue ?: 0.00

        val direction = atan2(nextLocationLatitude - locationLatitude, nextLocationLongitude - locationLongitude)
        if(direction > 0){
            rightCount++
        } else {
            leftCount++
        }
    }

    val leftSize = (leftCount.toFloat() / total.toFloat())
    val rightSize = (rightCount.toFloat() / total.toFloat())

    val averageLeft = leftCount * 100 / total
    val averageRight = rightCount * 100 / total
    Log.d(TAG, "Average left: $averageLeft")
    Log.d(TAG, "Average right: $averageRight")

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
        Canvas(modifier = Modifier.matchParentSize()) {
            drawIntoCanvas { canvas ->

                val leftRectLeftPoint = size.width/2-leftSize*size.width/2
                val rightRectRightPoint = size.width/2 + rightSize*size.width/4

                //Left rectangle
                drawRect(
                    color = colorLeft,
                    topLeft = Offset(leftRectLeftPoint, 0f),
                    size = androidx.compose.ui.geometry.Size(
                        leftSize*size.width/2,
                        size.height
                    )
                )

                // Right rectangle
                drawRect(
                    color = colorRight,
                    topLeft = Offset(size.width/2, 0f),
                    size = androidx.compose.ui.geometry.Size(
                        rightSize*size.width/2,
                        size.height)
                )

                // Left Arrow parameters
                /*val leftarrowColor = colorLeft
                val leftarrowHeadLength =  size.width/2 * leftSize / 2
                val leftarrowHeadWidth = size.height

                //Left Arrow
                val leftArrowPath = Path().apply {
                    moveTo(leftRectLeftPoint + leftRectLeftPoint/2 - leftarrowHeadLength, size.height / 2) // middle point
                    lineTo(leftRectLeftPoint + leftRectLeftPoint/2 +1, size.height / 2 - leftarrowHeadWidth / 2) // top point
                    lineTo(leftRectLeftPoint + leftRectLeftPoint/2 +1, size.height / 2 + leftarrowHeadWidth / 2)
                    close()
                }
                drawPath(
                    path = leftArrowPath,
                    color = leftarrowColor,
                )

                // Right Arrow parameters
                val rightarrowColor = colorRight
                val rightarrowHeadLength = size.width/2 * leftSize / 2
                val rightarrowHeadWidth = size.height

                // Right Arrow
                val rightArrowPath = Path().apply {
                    moveTo(rightRectRightPoint + rightarrowHeadLength, size.height / 2)
                    lineTo(rightRectRightPoint -1, size.height / 2 - rightarrowHeadWidth / 2)
                    lineTo(rightRectRightPoint -1, size.height / 2 + rightarrowHeadWidth / 2)
                    close()
                }
                drawPath(
                    path = rightArrowPath,
                    color = rightarrowColor,
                )*/

                // Add labels to the rectangles
                val textLeft = textMeasurer.measure(
                    text = "${averageLeft}%",
                    style = TextStyle(color = fontcolorLeft, fontSize = 40.sp, fontFamily = LeagueGothic)
                )
                val textRight = textMeasurer.measure(
                    text = "${averageRight}%",
                    style = TextStyle(color = fontcolorRight, fontSize = 40.sp, fontFamily = LeagueGothic)
                )

                drawText(
                    textLeft,
                    topLeft = Offset(size.width/4-50, size.height/2-60)
                )
                drawText(
                    textRight,
                    topLeft = Offset(size.width - size.width/4-50, size.height/2-60)
                )

                /*// Arrow parameters
                val arrowColor = white
                val arrowHeadLength = 50f
                val arrowHeadWidth = 30f
                val arrowOffset = 5.dp.toPx()

                //Left Arrow
                val leftArrowPath = Path().apply {
                    moveTo(size.width / 4 - 60 - arrowOffset - arrowHeadLength, size.height / 2)
                    lineTo(size.width / 4 - 60 - arrowOffset, size.height / 2 - arrowHeadWidth / 2)
                    lineTo(size.width / 4 - 60 - arrowOffset, size.height / 2 + arrowHeadWidth / 2)
                    close()
                }

                drawPath(
                    path = leftArrowPath,
                    color = arrowColor,
                )

                //right arrow
                val rightArrowPath = Path().apply {
                    moveTo(size.width - size.width / 4 + 60 + arrowOffset + arrowHeadLength, size.height / 2)
                    lineTo(size.width - size.width / 4 + 60 + arrowOffset, size.height / 2 - arrowHeadWidth / 2)
                    lineTo(size.width - size.width / 4 + 60 + arrowOffset, size.height / 2 + arrowHeadWidth / 2)
                    close()
                }

                drawPath(
                    path = rightArrowPath,
                    color = arrowColor,
                )*/

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