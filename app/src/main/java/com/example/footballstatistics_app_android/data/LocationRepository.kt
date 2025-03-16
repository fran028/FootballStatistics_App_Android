package com.example.footballstatistics_app_android.data

import kotlinx.coroutines.flow.Flow

class LocationRepository (private val locationDao: LocationDao){
    suspend fun insertLocation(location: Location) {
        locationDao.insertLocation(location)
    }

    fun getAllLocations(): Flow<List<Location>> {
        return locationDao.getAllLocations()
    }

    suspend fun updateLocation(location: Location) {
        locationDao.updateLocation(location)
    }

    suspend fun getLocationById(locationId: String): Location? {
        return locationDao.getLocationById(locationId)
    }

    suspend fun getLocationsByMatchId(matchId: String): List<Location>? {
        return locationDao.getLocationsByMatchId(matchId)

    }

    suspend fun checkIfMatchHasLocation(matchId: String): Boolean {
        return locationDao.checkIfMatchHasLocation(matchId)
    }

    suspend fun insertAllLocations(locations: List<Location>){
        locationDao.insertAllLocations(locations)
    }

}