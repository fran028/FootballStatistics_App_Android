package com.example.footballstatistics_app_android.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatistics_app_android.FootballStatisticsApplication
import com.example.footballstatistics_app_android.data.Match
import com.example.footballstatistics_app_android.data.MatchRepository
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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


    fun getMatchesBetweenDates(startDate: String, endDate: String, userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val matchList = repository.getMatchesBetweenDates(startDate, endDate, userId)
            withContext(Dispatchers.Main) {
                _matchesBetweenDates.value = matchList
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

    fun getTotalDuration(){
       viewModelScope.launch(Dispatchers.IO) {
           val totalDuration = repository.getTotalDuration()
           withContext(Dispatchers.Main) {
               _totalDuration.value = totalDuration
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
            val context = FootballStatisticsApplication.getContext()
            val exampleTAG = "Game Generator"
            val exampleMatch = Match(
                id = 0,
                user_id = userId,
                date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                ini_time = "00:00",
                end_time = "01:00",
                total_time = "60:00",
                away_corner_location = "51.7531458,-1.2282278",
                home_corner_location = "51.7532447,-1.2283335",
                kickoff_location = "51.7532636,-1.2282153",
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

    //suspend fun getMatchById(matchId: String) = repository.getMatchById(matchId)




}