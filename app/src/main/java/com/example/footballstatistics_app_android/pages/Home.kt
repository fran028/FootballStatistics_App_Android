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
import java.util.Locale
import kotlinx.coroutines.async

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

    var newUser by remember { mutableStateOf(true) }
    var lastMatch = matchViewModel.emptyMatch()
    if (lastMatches != null && lastMatches!!.isNotEmpty()) {
        lastMatch = lastMatches!![0] ?: matchViewModel.emptyMatch()
    }

    var hasCreatedMatch by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = userViewModel.loginUser, key2 = matchViewModel.lastMatch) {
        coroutineScope.launch(Dispatchers.IO) {
            // Use async to start both operations concurrently
            val userDeferred = async { userViewModel.getLoginUser() }
            val matchesDeferred = async { matchViewModel.getLastMatches() }

            // Wait for both operations to complete
            userDeferred.await()
            matchesDeferred.await()
            Log.d("HomePage", "User and matches fetched")
            val loginUser = userViewModel.loginUser.value
            Log.d("HomePage", "User fetched: $loginUser")
            // Now you can use the results safely
            if (loginUser != null) {
                Log.d("HomePage", "Getting last match for user: ${loginUser.id}")
                newUser = matchViewModel.lastMatch.value.isEmpty()
                Log.d("HomePage", "Add match for user: ${loginUser.id}")
                if (!hasCreatedMatch && newUser) {
                    try {
                        matchViewModel.getMatchCount(loginUser.id)
                        if (matchViewModel.matchCount.value == 0) {
                            Log.d(
                                "HomePage",
                                "No matches found for user: ${userViewModel.loginUser}"
                            )
                            // Add an example match for the logged-in user
                            val maxLatitude = 60.0
                            val maxLongitude = 100.0
                            val exampleMatch = Match(
                                id = "0",
                                user_id = loginUser!!.id,
                                date = LocalDate.now()
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                ini_time = "00:00",
                                end_time = "00:00",
                                total_time = "60:00",
                                away_corner_location = "0.00,$maxLongitude",
                                home_corner_location = "$maxLatitude,0.00",
                                kickoff_location = "${maxLatitude / 2},${maxLongitude / 2}"
                            )
                            Log.d(
                                "HomePage",
                                "Adding example match for user: ${loginUser.id}"
                            )
                            matchViewModel.insertMatch(exampleMatch)

                            val matchtime = 60
                            val matchtimeseconds = matchtime * 60
                            Log.d("HomePage", "Adding locations for user: ${loginUser.id}")

                            var prevPosition = Pair(maxLatitude / 2, maxLongitude / 2)
                            for (i in 1..matchtimeseconds) {
                                val nextposition = GetNextPosition(
                                    prevPosition.first,
                                    prevPosition.second,
                                    maxLatitude,
                                    maxLongitude
                                )
                                val location = Location(
                                    id = i,
                                    latitude = generateRandomLatitudeString(maxLatitude),
                                    longitude = generateRandomLongitudeString(maxLongitude),
                                    match_id = "0",
                                    timestamp = i.toString()

                                )
                                prevPosition = nextposition
                                //Log.d( "HomePage", "Location: ${location.latitude}, ${location.longitude}" )
                                locationViewModel.insertLocation(location)
                            }
                            Log.d("HomePage", "Locations added for user: ${loginUser!!.id}")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    hasCreatedMatch = true
                    Log.d("HomePage", "Match added for user: ${loginUser!!.id}")

                }
            }
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

fun GetNextPosition(latitude: Double, longitude: Double, maxLatitude: Double, maxLongitude: Double):Pair<Double, Double> {
    val randomLatitude = Random.nextDouble(latitude - 1, latitude + 1)
    val randomLongitude = Random.nextDouble(longitude - 1, longitude + 1)
    return Pair(randomLatitude, randomLongitude)
}

fun generateRandomLatitudeString(maxValue: Double): String {
    val randomLatitude = Random.nextDouble(0.00, maxValue)
    return String.format(Locale.UK, "%.6f", randomLatitude)
}

fun generateRandomLongitudeString(maxValue: Double): String {
    val randomLongitude = Random.nextDouble(0.00, maxValue)
    return String.format(Locale.UK, "%.6f", randomLongitude)
}