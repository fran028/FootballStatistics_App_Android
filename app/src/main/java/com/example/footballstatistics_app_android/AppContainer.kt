package com.example.footballstatistics_app_android

import android.content.Context
import com.example.footballstatistics_app_android.data.AppDatabase

class AppContainer(private val applicationContext: Context) {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(applicationContext) }
    fun getContext():Context{
        return applicationContext
    }
}