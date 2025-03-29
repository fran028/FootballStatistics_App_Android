package com.example.footballstatistics_app_android

import android.content.Context
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.data.UserRepository

interface AppContainer {
    val locationRepository: LocationRepository
    val matchRepository: MatchRepository
    val userRepository: UserRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val database: AppDatabase = AppDatabase.getDatabase(context)
    override val locationRepository: LocationRepository by lazy {
        LocationRepository(database.locationDao())
    }
    override val matchRepository: MatchRepository by lazy {
        MatchRepository(database.matchDao())
    }
    override val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }
}