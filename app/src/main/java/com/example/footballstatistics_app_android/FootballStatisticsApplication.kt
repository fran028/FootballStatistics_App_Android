package com.example.footballstatistics_app_android

import android.app.Application

class FootballStatisticsApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}