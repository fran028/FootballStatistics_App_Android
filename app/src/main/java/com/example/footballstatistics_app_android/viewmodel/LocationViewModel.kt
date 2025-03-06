package com.example.footballstatistics_app_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatistics_app_android.data.Location
import com.example.footballstatistics_app_android.data.LocationRepository
import kotlinx.coroutines.launch

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

    fun getLocationsByMatchId(matchId: String) {
        viewModelScope.launch {
            repository.getLocationsByMatchId(matchId)
        }
    }

    fun checkIfMatchHasLocation(matchId: String) {
        viewModelScope.launch {
            repository.checkIfMatchHasLocation(matchId)
        }
    }


}