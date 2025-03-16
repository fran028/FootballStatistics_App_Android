package com.example.footballstatistics_app_android.pages


import HeatmapChart
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Screen
import com.example.footballstatistics_app_android.Theme.LeagueGothic
import com.example.footballstatistics_app_android.Theme.RobotoCondensed
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.green
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.components.ButtonIconObject
import com.example.footballstatistics_app_android.components.ViewTitle
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.Location
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.data.Match
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.data.UserRepository
import com.example.footballstatistics_app_android.viewmodel.LocationViewModel
import com.example.footballstatistics_app_android.viewmodel.LocationViewModelFactory
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModelFactory
import com.example.footballstatistics_app_android.viewmodel.UserViewModel
import com.example.footballstatistics_app_android.viewmodel.UserViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import kotlinx.coroutines.async
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController) {

    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val matchRepository = MatchRepository(database.matchDao())
    val matchViewModelFactory = MatchViewModelFactory(matchRepository)
    val matchViewModel: MatchViewModel = viewModel(factory = matchViewModelFactory)
    val userRepository = UserRepository(database.userDao())
    val userViewModelFactory = UserViewModelFactory(userRepository)
    val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)
    val locationRepository = LocationRepository(database.locationDao())
    val locationViewModelFactory = LocationViewModelFactory(locationRepository)
    val locationViewModel: LocationViewModel = viewModel(factory = locationViewModelFactory)
    val coroutineScope = rememberCoroutineScope()

    val lastMatches by matchViewModel.lastMatch.collectAsState(initial = null)
    val loginUser by userViewModel.loginUser.collectAsState(initial = null)
    userViewModel.getLoginUser()

    var lastMatch = matchViewModel.emptyMatch()
    if (lastMatches != null && lastMatches!!.isNotEmpty()) {
        lastMatch = lastMatches!![0] ?: matchViewModel.emptyMatch()
    }

    LaunchedEffect(key1 = userViewModel.loginUser) {
        val user = userViewModel.loginUser.value
        if (user != null) {
            matchViewModel.getLastMatches(user.id.toString())
        }
    }

    LaunchedEffect(key1 = matchViewModel.lastMatch) {
        if (matchViewModel.lastMatch.value.isNotEmpty()) {
            lastMatch = lastMatches!![0] ?: matchViewModel.emptyMatch()
        }
    }

    val scrollState = rememberScrollState()

    var noMatches = true
    if(lastMatches != null) {
        if (lastMatches!!.isNotEmpty()) {
            noMatches = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(title = "FULBO STATS", R.drawable.home_img)
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)) {
            Column {
                Text(
                    text = "LAST MATCH PLAYED",
                    fontFamily = LeagueGothic,
                    fontSize = 40.sp,
                    color = white
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lastMatch.date,
                    fontFamily = RobotoCondensed,
                    fontSize = 32.sp,
                    color = white
                )
            }
            Image(
                painter = painterResource(id = R.drawable.soccer_white),
                contentDescription = "Soccer Image",
                modifier = Modifier.size(70.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)) {
            if(noMatches) {
                Text(
                    text = "EmptyExample",
                    fontFamily = LeagueGothic,
                    fontSize = 32.sp,
                    color = white
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pitch_color),
                        contentDescription = "Soccer Image",
                        modifier = Modifier
                            .size(250.dp)
                            .rotate(90f)
                            .padding(0.dp)
                    )
                }
            } else {
                Text(
                    text = "GAME CHART - HEATMAP",
                    fontFamily = LeagueGothic,
                    fontSize = 32.sp,
                    color = white,
                )
                Spacer(modifier = Modifier.height(4.dp))
                HeatmapChart(lastMatch.id, green)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "LAST PLAYED MATCHES",
            fontFamily = LeagueGothic,
            fontSize = 40.sp,
            color = white,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column (Modifier.padding(horizontal = 30.dp )) {
            if (!noMatches){
                for (match in lastMatches!!) {
                    ButtonIconObject(
                        text = "Match ${match?.id}",
                        onClick = { navController.navigate(Screen.Match.route) },
                        bgcolor = yellow,
                        height = 50.dp,
                        textcolor = black,
                        icon = R.drawable.soccer,
                        value = "${match?.date}"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                ButtonIconObject(
                    text = "No matches found",
                    bgcolor = white,
                    height = 50.dp,
                    textcolor = black,
                    value = "",
                    icon = R.drawable.soccer,
                    onClick = {  }
                )
            }
        }
    }
}

fun GetRandomNextPosition(latitude: Double, longitude: Double, maxLatitude: Double, maxLongitude: Double, minLatitude: Double, minLongitude: Double):Pair<Double, Double> {
    var newLatitude = Random.nextDouble(latitude - 0.000002, latitude + 0.000002)
    var newLongitude = Random.nextDouble(longitude - 0.000002, longitude + 0.000002)
    var randomLatitude = newLatitude
    var randomLongitude = newLongitude
    while (
        randomLatitude < minLatitude ||
        randomLatitude > maxLatitude ||
        randomLongitude < minLongitude ||
        randomLongitude > maxLongitude
    ) {
        randomLatitude = Random.nextDouble(newLatitude - 0.000002, newLatitude + 0.000002)
        randomLongitude = Random.nextDouble(newLongitude - 0.000002, newLongitude + 0.000002)
    }
    newLatitude = randomLatitude
    newLongitude = randomLongitude
    return Pair(newLatitude, newLongitude)
}

//fun GetNextPosition(
//    currentLatitude: Double,  currentLongitude: Double,
//    maxLatitude: Double, maxLongitude: Double,
//    minLatitude: Double, minLongitude: Double,
//    prevLatitude: Double, prevLongitude: Double
//): Pair<Double, Double> {
//
//    val maxStepDistance = 0.0001 // Maximum distance (latitude or longitude) a player can move in one step
//    val changeDirectionProbability = 0.15 // Probability of changing direction
//    val restProbability = 0.15// Probability of stop
//    val speedLimit = 0.00005
//
//    var newLatitude = currentLatitude
//    var newLongitude = currentLongitude
//
//    // Rest
//    if (Random.nextDouble() < restProbability) {
//        return Pair(newLatitude, newLongitude)
//    }
//
//    var directionLatitude = if (Random.nextDouble() < changeDirectionProbability) {
//        Random.nextDouble(-1.0, 1.0)
//    } else {
//        prevLatitude - currentLatitude
//    }
//    var directionLongitude = if (Random.nextDouble() < changeDirectionProbability) {
//        Random.nextDouble(-1.0, 1.0)
//    } else {
//        prevLongitude - currentLongitude
//    }
//
//    // Limit change of direction
//    directionLatitude = max(min(directionLatitude, maxStepDistance), -maxStepDistance)
//    directionLongitude = max(min(directionLongitude, maxStepDistance), -maxStepDistance)
//    newLatitude += directionLatitude
//    newLongitude += directionLongitude
//
//    // Limit Speed
//    val movement = Pair(abs(newLatitude - currentLatitude), abs(newLongitude - currentLongitude))
//    if (movement.first > speedLimit) {
//        newLatitude = if (newLatitude > currentLatitude) currentLatitude + speedLimit else currentLatitude - speedLimit
//    }
//    if (movement.second > speedLimit) {
//        newLongitude = if (newLongitude > currentLongitude) currentLongitude + speedLimit else currentLongitude - speedLimit
//    }
//
//    // Ensure inside field
//    newLatitude = max(minLatitude, min(newLatitude, maxLatitude))
//    newLongitude = max(minLongitude, min(newLongitude, maxLongitude))
//
//    return Pair(newLatitude, newLongitude)
//}