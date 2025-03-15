package com.example.footballstatistics_app_android

import android.content.Context
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.data.MatchRepository

interface AppContainer {
    val locationRepository: LocationRepository
    val matchRepository: MatchRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val database: AppDatabase = AppDatabase.getDatabase(context)
    override val locationRepository: LocationRepository by lazy {
        LocationRepository(database.locationDao())
    }
    override val matchRepository: MatchRepository by lazy {
        MatchRepository(database.matchDao())
    }
}