package com.example.footballstatistics_app_android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedDataViewModel : ViewModel() {
    val serviceState = MutableLiveData<String>()
    val matchData = MutableLiveData<String>()
    val locationData = MutableLiveData<String>()
    val newMatchId = MutableLiveData<Int>()
    val finished = MutableLiveData<Boolean>()
    val progress = MutableLiveData<Int>()
    init {
        serviceState.value = "Not started"
        matchData.value = "Match data not available"
        locationData.value = "Location data not available"
        newMatchId.value = -1
        finished.value = false
        progress.value = 0

    }
}