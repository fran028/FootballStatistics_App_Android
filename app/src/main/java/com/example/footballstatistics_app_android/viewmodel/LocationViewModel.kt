package com.example.footballstatistics_app_android.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatistics_app_android.FootballStatisticsApplication
import com.example.footballstatistics_app_android.data.Location
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.data.Match
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import kotlin.text.toDouble
import android.location.Location as AndroidLocation

class LocationViewModel(private val repository: LocationRepository) : ViewModel() {

    val allLocations = repository.getAllLocations()

    fun insertLocation(location: Location) {
        viewModelScope.launch {
            repository.insertLocation(location)
        }
    }

    fun updateLocation(location: Location) {
        viewModelScope.launch {
            repository.updateLocation(location)
        }
    }

    fun getLocationById(locationId: String) {
        viewModelScope.launch {
            repository.getLocationById(locationId)
        }
    }

    fun insertLocationFromFile(fileName: String, matchId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = FootballStatisticsApplication.getContext()
                val assetManager = context.assets
                val inputStream = assetManager.open(fileName)
                val reader = CSVReader(InputStreamReader(inputStream))
                val lines = reader.readAll()
                reader.close()
                // Skip header
                lines.drop(1).forEach { row ->
                    val location = createLocationFromCsvRow(row, matchId)
                    if (location != null) {
                        repository.insertLocation(location)
                    }
                }
                Log.d("LocationViewModel", "CSV locations inserted in database")
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Error inserting locations from CSV file", e)
            }
        }
    }

    private fun createLocationFromCsvRow(row: Array<String>, matchId: String): Location? {
        // Check if the row has the correct number of columns
        if (row.size != 5) {
            Log.e("LocationViewModel", "Skipping row with incorrect number of columns: ${row.contentToString()}")
            return null
        }
        return try {
            Location(
                match_id = matchId,
                latitude = row[2],
                longitude = row[3],
                timestamp = row[4],
            )
        } catch (e: NumberFormatException) {
            Log.e("LocationViewModel", "Error parsing numbers from row: ${row.contentToString()}", e)
            null
        } catch (e: Exception) {
            Log.e("LocationViewModel", "Error creating location from row: ${row.contentToString()}", e)
            null
        }
    }


    private val _locations = MutableStateFlow<List<Location>>(listOf())
    val locations: StateFlow<List<Location?>> = _locations
    private val _topSpeed = MutableStateFlow(0.0)
    val topSpeed: StateFlow<Double> = _topSpeed
    private val _totalDistance = MutableStateFlow(0.0)
    val totalDistance: StateFlow<Double> = _totalDistance
    private val _averageSpeed = MutableStateFlow(0.0)
    val averageSpeed: StateFlow<Double> = _averageSpeed

    fun getLocationsByMatchId(matchId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val locations = repository.getLocationsByMatchId(matchId)
            withContext(Dispatchers.Main) {
                if (locations != null) {
                    _locations.value = locations
                }
            }
        }
    }

    suspend fun checkIfMatchHasLocation(matchId: String): Boolean {
        Log.d("LocationViewModel", "Checking if match has location for match ID: $matchId")
        val hasLocation = repository.checkIfMatchHasLocation(matchId)
        Log.d("LocationViewModel", "Match ID $matchId has location: $hasLocation")
        return hasLocation
    }

    fun calculateTotalDistanceForMatch(matchId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val locations = repository.getLocationsByMatchId(matchId)
            if (locations != null) {
                val totalDistance = calculateTotalDistance(locations) / 1000
                _totalDistance.value = totalDistance
                Log.d("LocationViewModel", "Total distance for match $matchId: $totalDistance meters")
            } else {
                Log.d("LocationViewModel", "No locations found for match: $matchId")
            }
        }
    }

    fun calculateTopSpeedForMatch(matchId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val locations = repository.getLocationsByMatchId(matchId)
            if (locations != null) {
                val topSpeed = calculateTopSpeed(locations)
                _topSpeed.value = topSpeed
                Log.d("LocationViewModel", "Top speed for match $matchId: $topSpeed m/s")
            } else {
                Log.d("LocationViewModel", "No locations found for match: $matchId")
            }
        }
    }

    fun calculateAverageSpeedForMatch(matchId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val locations = repository.getLocationsByMatchId(matchId)
            if (locations != null) {
                val averageSpeed = calculateAverageSpeed(locations)
                _averageSpeed.value = averageSpeed
                Log.d("LocationViewModel", "Average speed for match $matchId: $averageSpeed min/km")
            } else {
                Log.d("LocationViewModel", "No locations found for match: $matchId")
            }
        }
    }

    private fun calculateTotalDistance(locations: List<Location>): Double {
        var totalDistance = 0.0
        if (locations.size < 2) {
            return totalDistance
        }
        for (i in 0 until locations.size - 1) {
            val loc1 = locations[i]
            val loc2 = locations[i + 1]

            val result = FloatArray(1)
            AndroidLocation.distanceBetween(
                loc1.latitude.toDouble(),
                loc1.longitude.toDouble(),
                loc2.latitude.toDouble(),
                loc2.longitude.toDouble(),
                result
            )
            totalDistance += result[0].toDouble()
        }
        return totalDistance
    }

    private fun calculateTopSpeed(locations: List<Location>, unit: SpeedUnit = SpeedUnit.KMH): Double {
        var topSpeed = 0.0
        if (locations.size < 2) {
            return topSpeed
        }

        for (i in 0 until locations.size - 1) {
            val loc1 = locations[i]
            val loc2 = locations[i + 1]

            // Calculate time difference in seconds
            val timeDiffSeconds = (loc2.timestamp.toDouble() - loc1.timestamp.toDouble()).toDouble() / 1000.0

            // Avoid division by zero
            if (timeDiffSeconds <= 0) continue

            // Calculate distance
            val result = FloatArray(1)
            AndroidLocation.distanceBetween(
                loc1.latitude.toDouble(),
                loc1.longitude.toDouble(),
                loc2.latitude.toDouble(),
                loc2.longitude.toDouble(),
                result
            )
            val distance = result[0].toDouble()

            // Calculate speed
            val speed = distance / timeDiffSeconds

            // Update topSpeed if needed
            if (speed > topSpeed) {
                topSpeed = speed
            }
        }
        val convertedSpeed = when (unit) {
            SpeedUnit.MS -> topSpeed
            SpeedUnit.KMH -> topSpeed * 3.6
            SpeedUnit.MPH -> topSpeed * 2.237
        }
        return convertedSpeed
    }
    enum class SpeedUnit {
        MS, // Meters per second
        KMH, // Kilometers per hour
        MPH // Miles per hour
    }

    private fun calculateAverageSpeed(locations: List<Location>): Double {
        val totalDistanceMeters = calculateTotalDistance(locations)
        Log.d("LocationViewModel", "Total distance for calculating speed: $totalDistanceMeters meters")
        if (totalDistanceMeters == 0.0 || locations.size < 2) {
            return 0.0
        }

        val firstTimestamp = locations.first().timestamp.toDouble()
        val lastTimestamp = locations.last().timestamp.toDouble()
        // Convert the difference to seconds and then make it a double to avoid int division
        val totalTimeSeconds = (lastTimestamp - firstTimestamp) / 1000.0
        Log.d("LocationViewModel", "Total time for calculating speed: $totalTimeSeconds seconds")

        // Convert total distance to kilometers
        val totalDistanceKm = totalDistanceMeters / 1000.0
        Log.d("LocationViewModel", "Total distance in kilometers for calculating speed: $totalDistanceKm km")
        val totalTimeHours = totalTimeSeconds / 3600.0
        Log.d("LocationViewModel", "Total time in hours for calculating speed: $totalTimeHours hours")
        // Calculate average speed in hour per kilometer
        val averageSpeedHourPerKm = if (totalDistanceKm > 0) {
            totalDistanceKm / totalTimeHours
        } else {
            0.0
        }
        Log.d("LocationViewModel", "Average speed in hours per km: $averageSpeedHourPerKm hours/km")

        // Calculate average speed in minutes per kilometer
        val averageSpeedMinPerKm = if (totalDistanceKm > 0) {
            totalDistanceKm/ totalTimeSeconds / 60.0
        } else {
            0.0
        }
        Log.d("LocationViewModel", "Average speed in minutes per km: $averageSpeedMinPerKm min/km")

        return averageSpeedHourPerKm
    }

    private val _allMatchesDistance = MutableStateFlow(0.0)
    val allMatchesDistance: StateFlow<Double> = _allMatchesDistance

    fun getDistanceOfAllMatches(matches: List<Match?>){
        var totalDistance = 0.0
        viewModelScope.launch(Dispatchers.IO) {
            for (match in matches) {
                val matchId = match?.id.toString()
                val locations = repository.getLocationsByMatchId(matchId)
                if (locations != null) {
                    val matchDistance = calculateTotalDistance(locations) / 1000
                    totalDistance += totalDistance
                    Log.d(
                        "LocationViewModel",
                        "Total distance for match $matchId: $totalDistance meters"
                    )
                } else {
                    Log.d("LocationViewModel", "No locations found for match: $matchId")
                }
            }
            _allMatchesDistance.value = totalDistance
        }
    }

    fun deleteLocationsFromMatch(matchId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val locations = repository.getLocationsByMatchId(matchId)
            if (locations != null) {
                for (location in locations) {
                    repository.deleteLocation(location)
                    Log.d("LocationViewModel", "Location deleted: $location")
                }
                Log.d("LocationViewModel", "All locations for match $matchId deleted")
            } else {
                Log.d("LocationViewModel", "No locations found for match: $matchId")
            }
        }
    }
}