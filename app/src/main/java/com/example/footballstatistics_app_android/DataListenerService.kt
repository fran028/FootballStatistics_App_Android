package com.example.footballstatistics_app_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.input.key.type
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import androidx.core.app.NotificationCompat
import com.example.footballstatistics_app_android.data.Location
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.data.Match
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.viewmodel.LocationViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object Constants {
    const val DATA_TRANSFER_COMPLETE = "com.example.footballstatistics_app_android.DATA_TRANSFER_COMPLETE"
    const val DATALISTENER_NOTIFICATION_CHANNEL_ID = "DataListenerServiceChannel"
    const val DATALISTENER_NOTIFICATION_ID = 1001
}

private const val TAG = "DataListenerService"

class DataListenerService : WearableListenerService(), DataClient.OnDataChangedListener {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var matchViewModel: MatchViewModel
    private lateinit var locationRepository: LocationRepository
    private lateinit var matchRepository: MatchRepository
    private lateinit var container: AppContainer
    private lateinit var dataClient: DataClient
    private var currentMatchId: Int = -1 // Initialize with a default value

    init {
        Log.d(TAG, "Service Constructor called")
    }

    @Override
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand initiated")
        super.onCreate()
        container = (applicationContext as FootballStatisticsApplication).container
        locationRepository = container.locationRepository
        matchRepository = container.matchRepository
        locationViewModel = LocationViewModel(locationRepository)
        matchViewModel = MatchViewModel(matchRepository)
        dataClient = Wearable.getDataClient(this)

        val nodeClient: NodeClient = Wearable.getNodeClient(this) // Initialize NodeClient
        isSmartwatchNodeConnected(nodeClient) { isConnected, smartwatchNode ->
            if (isConnected && smartwatchNode != null) {
                val nodeId = smartwatchNode.id
                val displayName = smartwatchNode.displayName
                Log.d(TAG, "Smartwatch connected! Node ID: $nodeId, Display Name: $displayName")
            } else {
                Log.d(TAG, "No smartwatch is connected.")
            }
        }

        Log.d(TAG, "createNotificationChannel")
        createNotificationChannel()
        Log.d(TAG, "startForeground")
        startForeground(Constants.DATALISTENER_NOTIFICATION_ID, createNotification())
        Log.d(TAG, "addListener")
        dataClient.addListener(this)
        Log.d(TAG, "onCreate Ended")
        return START_STICKY
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "onDataChanged: $dataEvents")
        try{
            for (event in dataEvents) {
                if (event.type == DataEvent.TYPE_CHANGED) {
                    val dataItem = event.dataItem
                    when (dataItem.uri.path) {
                        "wear:/location_data" -> {
                            if (currentMatchId != -1) {
                                val dataMapItem = DataMapItem.fromDataItem(dataItem)
                                val locationDataList = dataMapItem.dataMap.getStringArrayList("location_data")
                                if (locationDataList != null) {
                                    processLocationData(locationDataList, currentMatchId)
                                }
                            } else {
                                Log.e(TAG, "Received location data before match data")
                            }
                        }
                        "wear:/match_data" -> {
                            val dataMapItem = DataMapItem.fromDataItem(dataItem)
                            val matchJson = dataMapItem.dataMap.getString("matches")
                            if (matchJson != null) {
                                processMatchData(matchJson)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in onDataChanged: ${e.message}", e)
        }
    }

    fun isSmartwatchNodeConnected(nodeClient: NodeClient, onConnectionResult: (Boolean, Node?) -> Unit) {
        nodeClient.connectedNodes.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val nodes: List<Node> = task.result
                // Assuming only one Wear OS device will be connected at a time
                val smartwatchNode: Node? = nodes.firstOrNull { it.isNearby }
                Log.d(TAG, "Smartwatch Node: $smartwatchNode")
                onConnectionResult(smartwatchNode != null, smartwatchNode)
            } else {
                // Handle error (e.g., log it)
                Log.e(TAG, "Error getting connected nodes: ${task.exception}")
                onConnectionResult(false, null)
            }
        }
    }

    private fun processMatchData(matchJson: String) {
        coroutineScope.launch {
            try {
                val gson = Gson()
                val match: Match = gson.fromJson(matchJson, Match::class.java)
                matchViewModel.insertMatch(match)
                currentMatchId = match.id // Assuming match.id is an Int or Long
                Log.d(TAG, "Match data saved to database with ID: $currentMatchId")
                // Send the broadcast
                sendDataTransferCompleteBroadcast()
            } catch (e: Exception) {
                Log.e(TAG, "Error processing match data", e)
            }
        }
    }

    private fun processLocationData(locationDataList: ArrayList<String>, matchId: Int) {
        coroutineScope.launch {
            try {
                val locations = mutableListOf<Location>()
                locationDataList.forEach { locationData ->
                    val parts = locationData.split(",")
                    if (parts.size == 3) {
                        val latitude = parts[0].toDoubleOrNull()
                        val longitude = parts[1].toDoubleOrNull()
                        val timestamp = parts[2].toLongOrNull()

                        if (latitude != null && longitude != null && timestamp != null) {
                            locations.add(
                                Location(
                                    latitude = latitude.toString(),
                                    longitude = longitude.toString(),
                                    timestamp = timestamp.toString(),
                                    match_id = matchId.toString() // Use the provided matchId here
                                )
                            )
                        }
                    }
                }
                locations.forEach { locationViewModel.insertLocation(it) }
                Log.d(TAG, "Location Data saved to database for match ID: $matchId")
                // Send the broadcast
                sendDataTransferCompleteBroadcast()
            } catch (e: Exception) {
                Log.e(TAG, "Error processing location data", e)
            }
        }
    }

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