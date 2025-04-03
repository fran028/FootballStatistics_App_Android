package com.example.footballstatistics_app_android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedDataViewModel : ViewModel() {
    val serviceState = MutableLiveData<String>().apply { value = "Not started" }
    val serviceStage = MutableLiveData<Int>().apply { value = 0 } // serviceStage  = 0: Not started, 1: Started, 2: Listening for new match, 3: Match found, 4: Data received
    val matchData = MutableLiveData<String>().apply { value = "Match data not available" }
    val locationData = MutableLiveData<String>().apply { value = "Location data not available" }
    val newMatchId = MutableLiveData<Int>().apply { value = -1 }
    val finished = MutableLiveData<Boolean>().apply { value = false }

    companion object {
        @Volatile // Ensure visibility of changes across threads
        private var instance: SharedDataViewModel? = null

        fun getInstance(): SharedDataViewModel {
            return instance ?: synchronized(this) {
                instance ?: SharedDataViewModel().also { instance = it }
            }
        }

        fun getServiceStage(): Int? {
            return instance?.serviceStage?.value
        }
    }
}