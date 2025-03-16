package com.example.footballstatistics_app_android

import android.app.Application
import android.content.Context
import android.util.Log

class FootballStatisticsApplication : Application() {
    init {
        Log.d(TAG, "init FootballStatisticsApplication")
    }
    companion object {
        lateinit var instance: FootballStatisticsApplication
            private set

        private const val TAG = "FootballStatisticsApplication"
        fun getContext(): Context {
            return instance.applicationContext
        }
    }
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        instance = this // Initialize the instance here
        container = DefaultAppContainer(this)
    }
}