package com.example.footballstatistics_app_android.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.footballstatistics_app_android.data.Match
import com.example.footballstatistics_app_android.data.MatchRepository
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

    private val _matchCount = MutableStateFlow<Int>(0)
    val matchCount: StateFlow<Int> = _matchCount

    private val _totalDuration = MutableStateFlow<String>("00:00:00")
    val totalDuration: StateFlow<String> = _totalDuration

    fun getLastMatches() {
        viewModelScope.launch(Dispatchers.IO) {
            val matchList = repository.getLastMatches(5)
            withContext(Dispatchers.Main) {
                _lastMatch.value = matchList
            }
        }
    }

    fun getMatch(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val match = repository.getMatchById(id)
            withContext(Dispatchers.Main) {
                _match.value = match
            }
        }
    }


    fun getMatchCount(){
        viewModelScope.launch(Dispatchers.IO) {
            val matchCount = repository.getMatchCount()
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
        var emptymatch = Match(
            id = "1",
            user_id = "1",
            date = date,
            ini_time = "00:00",
            end_time = "00:00",
            total_time = "60:00",
            away_corner_location = "0",
            home_corner_location = "0",
            kickoff_location = "0"
        )
        return emptymatch
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

    fun getLastMatches(amount: Int) = repository.getLastMatches(amount)

    fun getMatchesBetweenDates(startDate: String, endDate: String) = repository.getMatchesBetweenDates(startDate, endDate)


}