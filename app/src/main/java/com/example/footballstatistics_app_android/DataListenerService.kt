package com.example.footballstatistics_app_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.input.key.type
import androidx.core.app.NotificationCompat
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

object Constants {
    const val DATA_TRANSFER_COMPLETE = "com.example.footballstatistics_app_android.DATA_TRANSFER_COMPLETE"
    const val DATALISTENER_NOTIFICATION_CHANNEL_ID = "DataListenerServiceChannel"
    const val DATALISTENER_NOTIFICATION_ID = 1001
}

class DataListenerService : WearableListenerService() {
    val TAG = "DataListenerService"
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var matchViewModel: MatchViewModel
    private lateinit var locationRepository: LocationRepository
    private lateinit var matchRepository: MatchRepository
    private lateinit var container: AppContainer
    private var matchesJsonList = mutableListOf<String>()
    private var locationDataJsonList = mutableListOf<String>()

    init {
        Log.d(TAG, "Service Constructor called")
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onCreate")
        super.onCreate()
        container = (applicationContext as FootballStatisticsApplication).container
        locationRepository = container.locationRepository
        matchRepository = container.matchRepository
        locationViewModel = LocationViewModel(locationRepository)
        matchViewModel = MatchViewModel(matchRepository)
        Log.d(TAG, "createNotificationChannel")
        createNotificationChannel()
        Log.d(TAG, "startForeground")
        startForeground(Constants.DATALISTENER_NOTIFICATION_ID, createNotification())
        Log.d(TAG, "onCreate Ended")
        return START_STICKY
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        Log.d(TAG, "onDataChanged")

        for (event in dataEvents) {
            Log.d(TAG, "Data event found: $event")
            if (event.type == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "Data changed")
                val dataItem = event.dataItem
                Log.d(TAG, "Data path: ${dataItem.uri.path}")
                if (dataItem.uri.path == "/transfer_data") {
                    Log.d(TAG, "Transfer data received")
                    val dataMapItem = DataMapItem.fromDataItem(dataItem)
                    val dataJson = dataMapItem.dataMap.getString("data")
                    Log.d(TAG, "DataMap data: $dataJson")
                    val gson = Gson()
                    val transferData = gson.fromJson(dataJson, TransferData::class.java)

                    when (transferData.type) {
                        "start" -> {
                            matchesJsonList.clear()
                            locationDataJsonList.clear()
                            Log.d(TAG, "Transfer started")
                        }
                        "matches" -> {
                            matchesJsonList.add(gson.toJson(transferData.data))
                        }
                        "location_data" -> {
                            locationDataJsonList.add(gson.toJson(transferData.data))
                        }
                        "end" -> {
                            Log.d(TAG, "Transfer ended")
                            processData(matchesJsonList.joinToString(), locationDataJsonList.joinToString())
                        }
                        else -> Log.d(TAG, "Unknown data type")
                    }
                }
            }
        }
    }

    private fun processData(matchesJson: String, locationDataJson: String){
        coroutineScope.launch {
            try {
                val gson = Gson()

                // Deserialize matches
                val matchType: Type = object : TypeToken<List<Match>>() {}.type
                val matches: List<Match> = gson.fromJson(matchesJson, matchType)

                // Deserialize locationData
                val locationDataType: Type = object : TypeToken<List<Location>>() {}.type
                val locationData: List<Location> = gson.fromJson(locationDataJson, locationDataType)

                // Save to the database
                matches.forEach { matchViewModel.insertMatch(it) }
                locationData.forEach { locationViewModel.insertLocation(it) }

                Log.d(TAG, "Data saved to database")
                // Send the broadcast
                sendDataTransferCompleteBroadcast()
            } catch (e: Exception) {
                Log.e(TAG, "Error processing data", e)
            }
        }
    }
    data class TransferData(val type: String, val data: Any)

    private fun sendDataTransferCompleteBroadcast() {
        val broadcastIntent = Intent(Constants.DATA_TRANSFER_COMPLETE).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            }
        }
        sendBroadcast(broadcastIntent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.POST_NOTIFICATIONS else null)
        Log.d(TAG, "Broadcast sent")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                Constants.DATALISTENER_NOTIFICATION_CHANNEL_ID,
                "Data Listener Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, Constants.DATALISTENER_NOTIFICATION_CHANNEL_ID)
        .setContentTitle("DataLayer Service")
        .setContentText("Running...")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .build()
}