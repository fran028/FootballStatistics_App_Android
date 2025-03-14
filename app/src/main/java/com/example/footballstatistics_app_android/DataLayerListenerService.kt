package com.example.footballstatistics_app_android

import android.util.Log
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.input.key.type
import com.example.footballstatistics_app_android.data.Location
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.data.Match
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.viewmodel.LocationViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class DataLayerListenerService : WearableListenerService() {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var matchViewModel: MatchViewModel
    private lateinit var locationRepository: LocationRepository
    private lateinit var matchRepository: MatchRepository
    private lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = (application as MainActivity).container
        locationViewModel = LocationViewModel(locationRepository)
        matchViewModel = MatchViewModel(matchRepository)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        Log.d("DataLayer", "onDataChanged")

        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/transfer_data") {
                    val dataMapItem = DataMapItem.fromDataItem(dataItem)
                    val matchesJson = dataMapItem.dataMap.getString("matches")
                    val locationDataJson = dataMapItem.dataMap.getString("location_data")

                    coroutineScope.launch {
                        try {
                            val gson = Gson()
                            // Deserialize matches
                            val matchType: Type = object : TypeToken<List<Match>>() {}.type
                            val matches: List<Match> = gson.fromJson(matchesJson, matchType)

                            // Deserialize locationData
                            val locationDataType: Type = object : TypeToken<List<Location>>() {}.type
                            val locationData: List<Location> =
                                gson.fromJson(locationDataJson, locationDataType)

                            // Save to the database
                            matches.forEach { matchViewModel.insertMatch(it) }
                            locationData.forEach { locationViewModel.insertLocation(it) }

                            Log.d("DataLayer", "Data saved to database")
                        } catch (e: Exception) {
                            Log.e("DataLayer", "Error processing data", e)
                        }
                    }
                }
            }
        }
    }
}