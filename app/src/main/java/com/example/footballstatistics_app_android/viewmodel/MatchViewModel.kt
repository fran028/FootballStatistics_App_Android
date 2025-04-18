package com.example.footballstatistics_app_android.viewmodel

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatistics_app_android.FootballStatisticsApplication
import com.example.footballstatistics_app_android.data.Match
import com.example.footballstatistics_app_android.data.MatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale

class MatchViewModel(private val repository: MatchRepository) : ViewModel() {

    private val _lastMatch = MutableStateFlow<List<Match?>>(listOf())
    val lastMatch: StateFlow<List<Match?>> = _lastMatch

    private val _match = MutableStateFlow<Match?>(null)
    val match: StateFlow<Match?> = _match

    private val _exampleMatch = MutableStateFlow<Match?>(null)
    val exampleMatch: StateFlow<Match?> = _exampleMatch

    private val _matchCount = MutableStateFlow<Int>(0)
    val matchCount: StateFlow<Int> = _matchCount

    private val _totalDuration = MutableStateFlow<String>("00:00:00")
    val totalDuration: StateFlow<String> = _totalDuration

    private val _matchesBetweenDates = MutableStateFlow<List<Match?>>(listOf())
    val matchesBetweenDates: StateFlow<List<Match?>> = _matchesBetweenDates

    private val _matchesInMonth = MutableStateFlow<List<Match?>>(listOf())
    val matchesInMonth: StateFlow<List<Match?>> = _matchesInMonth

    private val _allMatches = MutableStateFlow<List<Match?>>(listOf())
    val allMatches: StateFlow<List<Match?>> = _allMatches


    @RequiresApi(Build.VERSION_CODES.O)
    fun getMatchesBetweenDates(startDate: String, endDate: String, userId: String) {
        Log.d("MatchViewModel", "Fetching matches between dates: $startDate and $endDate , user: $userId")
        if (startDate == null || endDate == null) {
            Log.d("MatchViewModel", "Invalid date range")
            _matchesBetweenDates.value = emptyList()
            return
        }

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val startLocalDate: LocalDate
        val endLocalDate: LocalDate

        try {
            startLocalDate = LocalDate.parse(startDate, formatter)
            endLocalDate = LocalDate.parse(endDate, formatter)
        } catch (e: DateTimeParseException) {
            _matchesBetweenDates.value = emptyList()
            return
        }

        val formattedStartDate = startLocalDate.format(formatter)
        val formattedEndDate = endLocalDate.format(formatter)
        Log.d("MatchViewModel", "Formatted dates: $formattedStartDate and $formattedEndDate")

        viewModelScope.launch(Dispatchers.IO) {
            val matchList = repository.getMatchesBetweenDates(formattedStartDate, formattedEndDate, userId)
            Log.d("MatchViewModel", "Fetched ${matchList.size} matches")
            withContext(Dispatchers.Main) {
                _matchesBetweenDates.value = matchList
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMatchesInMonth(startDate: String, endDate: String, userId: String) {
        Log.d("MatchViewModel", "Fetching matches between dates: $startDate and $endDate")
        if (startDate == null || endDate == null) {
            Log.d("MatchViewModel", "Invalid date range")
            _matchesInMonth.value = emptyList()
            return
        }

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val startLocalDate: LocalDate
        val endLocalDate: LocalDate

        try {
            startLocalDate = LocalDate.parse(startDate, formatter)
            endLocalDate = LocalDate.parse(endDate, formatter)
        } catch (e: DateTimeParseException) {
            _matchesInMonth.value = emptyList()
            return
        }

        val formattedStartDate = startLocalDate.format(formatter)
        val formattedEndDate = endLocalDate.format(formatter)
        Log.d("MatchViewModel", "Formatted dates: $formattedStartDate and $formattedEndDate")

        viewModelScope.launch(Dispatchers.IO) {
            val matchList = repository.getMatchesBetweenDates(formattedStartDate, formattedEndDate, userId)
            Log.d("MatchViewModel", "Fetched ${matchList.size} matches")
            withContext(Dispatchers.Main) {
                _matchesInMonth.value = matchList
            }
        }
    }

    fun getLastMatches(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val matchList = repository.getLastMatches(5, userId)
            withContext(Dispatchers.Main) {
                _lastMatch.value = matchList
            }
        }
    }

    fun getMatch(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val match = repository.getMatchById(id)
            withContext(Dispatchers.Main) {
                _match.value = match
            }
        }
    }

    suspend fun getLastMatchId(userId: String): Int {
        return repository.getLastMatchId(userId)
    }

    suspend fun getExampleMatch(userId: String) = withContext(Dispatchers.IO) {
        try {
            val match = repository.getExampleMatch(userId)
            _exampleMatch.value = match
            Log.d("MatchViewModel", "Example Match fetched: ${match?.id}")
        } catch (e: Exception) {
            Log.e("MatchViewModel", "Error fetching example match for user: $userId", e)
            _exampleMatch.value = null
        }
    }


    fun getMatchCount(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            val matchCount = repository.getMatchCount(id)
            withContext(Dispatchers.Main) {
                _matchCount.value = matchCount
            }
        }
    }

    fun getTotalDuration(id: String){
       viewModelScope.launch(Dispatchers.IO) {
           val totalDuration = repository.getTotalDuration(id)
           withContext(Dispatchers.Main) {
               _totalDuration.value = totalDuration
           }
       }
    }

    fun getAllUserMatches(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            val matchList = repository.getAllUserMatches(id)
            withContext(Dispatchers.Main) {
                _allMatches.value = matchList
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodaysDate(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return today.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun emptyMatch(): Match{
        val date = this.getTodaysDate()
        val emptymatch = Match(
            id = 1,
            user_id = "1",
            date = date,
            ini_time = "00:00",
            end_time = "00:00",
            total_time = "60:00",
            away_corner_location = "0",
            home_corner_location = "0",
            kickoff_location = "0",
            isExample = false
        )
        return emptymatch
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertExampleMatch(userId: String) = withContext(Dispatchers.IO) {
        try {
            val exampleTAG = "Game Generator"
            val exampleMatch = Match(
                id = 0,
                user_id = userId,
                date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                ini_time = "21:30",
                end_time = "22:30",
                total_time = "00:60:00",
                away_corner_location = "51.75000, -1.22900",
                home_corner_location = "51.74946, -1.22780",
                kickoff_location = "51.74933, -1.215799",
                isExample = true

            )
            Log.d(exampleTAG, "Adding example match for user: ${userId}")
            repository.insertMatch(exampleMatch)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dayHasMatch(date: String, userId: String): Boolean {
        var hasMatch = false
        viewModelScope.launch(Dispatchers.IO) {
            val match = repository.getMatchesBetweenDates(date, date, userId)
            withContext(Dispatchers.Main) {
                hasMatch = match.isNotEmpty()
            }
        }
        return hasMatch
    }

    fun insertMatch(match: Match) {
        viewModelScope.launch {
            repository.insertMatch(match)
        }
    }

    fun updateMatch(match: Match) {
        viewModelScope.launch {
            repository.updateMatch(match)
        }
    }

    fun deleteMatch(match: Match) {
        viewModelScope.launch {
            repository.deleteMatch(match)
        }
    }


    val _pitchSizeHorizontal = MutableStateFlow(0.0)
    val pitchSizeHorizontal: StateFlow<Double> = _pitchSizeHorizontal
    val _pitchSizeVertical = MutableStateFlow(0.0)
    val pitchSizeVertical: StateFlow<Double> = _pitchSizeVertical

    fun getMatchAndCalculatePitchSize(matchId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val match = repository.getMatchById(matchId)
            if (match != null) {
                val size = pitchSize(match)
                if (size != null) {
                    _pitchSizeHorizontal.value = size.first
                    _pitchSizeVertical.value = size.second
                }
            }
        }
    }

    fun pitchSize(match: Match): Pair<Double, Double>? {
        val awayCorner = match.away_corner_location
        val homeCorner = match.home_corner_location

        if (awayCorner.isNullOrEmpty() || homeCorner.isNullOrEmpty()) {
            Log.e("MatchViewModel", "Corner locations are missing or invalid.")
            return null
        }

        val awayCornerCoords = parseCoordinates(awayCorner)
        val homeCornerCoords = parseCoordinates(homeCorner)

        if (awayCornerCoords == null || homeCornerCoords == null) {
            Log.e("MatchViewModel", "Failed to parse corner coordinates.")
            return null
        }

        val (awayLat, awayLon) = awayCornerCoords
        val (homeLat, homeLon) = homeCornerCoords

        val width = calculateDistance(awayLat, awayLon, awayLat, homeLon)
        val height = calculateDistance(awayLat, awayLon, homeLat, awayLon)

        Log.d("MatchViewModel", "Pitch size: width=$width meters, height=$height meters")
        return Pair(width, height)
    }

    private fun parseCoordinates(locationString: String): Pair<Double, Double>? {
        return try {
            val parts = locationString.split(",")
            if (parts.size == 2) {
                Pair(parts[0].toDouble(), parts[1].toDouble())
            } else {
                null
            }
        } catch (e: NumberFormatException) {
            Log.e("MatchViewModel", "Invalid coordinate format: $locationString", e)
            null
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val loc1 = Location("").apply {
            latitude = lat1
            longitude = lon1
        }
        val loc2 = Location("").apply {
            latitude = lat2
            longitude = lon2
        }

        return loc1.distanceTo(loc2).toDouble() // Returns distance in meters
    }


}