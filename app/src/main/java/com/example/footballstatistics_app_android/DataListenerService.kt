package com.example.footballstatistics_app_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.footballstatistics_app_android.data.Location
import com.example.footballstatistics_app_android.data.LocationRepository
import com.example.footballstatistics_app_android.data.Match
import com.example.footballstatistics_app_android.data.MatchRepository
import com.example.footballstatistics_app_android.data.UserRepository
import com.example.footballstatistics_app_android.viewmodel.LocationViewModel
import com.example.footballstatistics_app_android.viewmodel.MatchViewModel
import com.example.footballstatistics_app_android.viewmodel.SharedDataViewModel
import com.example.footballstatistics_app_android.viewmodel.UserViewModel
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
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collectLatest

object Constants {
    const val DATA_TRANSFER_COMPLETE = "com.example.footballstatistics_app_android.DATA_TRANSFER_COMPLETE"
    const val DATALISTENER_NOTIFICATION_CHANNEL_ID = "DataListenerServiceChannel"
    const val DATALISTENER_NOTIFICATION_ID = 1001
}

private const val TAG = "DataListenerService"

class DataListenerService : WearableListenerService(), DataClient.OnDataChangedListener,
    ViewModelStoreOwner {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var matchViewModel: MatchViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var locationRepository: LocationRepository
    private lateinit var matchRepository: MatchRepository
    private lateinit var userRepository: UserRepository
    private lateinit var container: AppContainer
    private lateinit var dataClient: DataClient
    private var currentMatchId: Int = -1 // Initialize with a default value
    private var loginUserId = "0"
    private var lastMatchId = "0"
    private lateinit var sharedViewModel: SharedDataViewModel

    override val viewModelStore: ViewModelStore by lazy { ViewModelStore() }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    init {
        Log.d(TAG, "Service Constructor called")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate called")
        container = (applicationContext as FootballStatisticsApplication).container
//        sharedViewModel = ViewModelProvider(this).get(SharedDataViewModel::class.java)
        sharedViewModel = SharedDataViewModel.getInstance()
        locationRepository = container.locationRepository
        matchRepository = container.matchRepository
        userRepository = container.userRepository
        locationViewModel = LocationViewModel(locationRepository)
        matchViewModel = MatchViewModel(matchRepository)
        userViewModel = UserViewModel(userRepository)
        dataClient = Wearable.getDataClient(this)
        Log.d(TAG, "View models created")

        userViewModel.getLoginUser()
        serviceScope.launch {
            userViewModel.loginUser.collectLatest { user ->
                val userId = user?.id
                if (userId != null) {
                    loginUserId = userId.toString()
                    val usermatchId =
                        withContext(Dispatchers.Main) { // Switch to main thread for database operation
                            matchViewModel.getLastMatchId(userId = loginUserId)
                        }
                    if (usermatchId != null) {
                        lastMatchId = usermatchId.toString()
                        currentMatchId = lastMatchId.toInt() + 1
                        Log.d(TAG, "Last Match ID: ${lastMatchId}")
                        // Do something with the last match ID (e.g., store it, use it for filtering data)
                    } else {
                        Log.d(TAG, "No previous matches found for user $userId")
                        // Handle the case where there are no previous matches
                    }
                } else {
                    Log.d(TAG, "No user logged in")
                    // Handle the case where no user is logged in (e.g., use a default ID)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
    }

    @Override
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand initiated")

        Log.d("DataListenerService", "ViewModel hash: ${sharedViewModel.hashCode()}, Updating serviceState: \"Service Started\"")
        sharedViewModel.serviceState.postValue("Service Started")
        sharedViewModel.serviceStage.postValue(1)

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
        Log.d("DataListenerService", "ViewModel hash: ${sharedViewModel.hashCode()}, Updating serviceState: \"Listening for new match\"")
        sharedViewModel.serviceState.postValue("Listening for new match")
        sharedViewModel.serviceStage.postValue(2)
        sharedViewModel.finished.postValue(false)
        Log.d(TAG, "onCreate Ended")
        return START_STICKY
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
//        if(SharedDataViewModel.getServiceStage()!! < 3){
//            sharedViewModel.serviceStage.postValue(3)
//        }
        Log.d(TAG, "onDataChanged called!")
        Log.d(TAG, "onDataChanged: $dataEvents")
        sharedViewModel.serviceState.postValue("Match found")
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
                        "wear:/transfer_data" -> {
                            val dataMapItem = DataMapItem.fromDataItem(dataItem)
                            val matchJsonStart = dataMapItem.dataMap.getString("start")
                            val matchJsonEnd = dataMapItem.dataMap.getString("end")
                            if (matchJsonStart != null) {
                                sharedViewModel.serviceStage.postValue(3)
                            }
                            if (matchJsonStart != null) {
                                sharedViewModel.serviceStage.postValue(4)
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
                val smartwatchNode: Node? = nodes.firstOrNull { it.isNearby }
                Log.d(TAG, "Smartwatch Node: $smartwatchNode")
                onConnectionResult(smartwatchNode != null, smartwatchNode)
            } else {
                Log.e(TAG, "Error getting connected nodes: ${task.exception}")
                onConnectionResult(false, null)
            }
        }
    }

    private fun processMatchData(matchJson: String) {
        coroutineScope.launch {
            try {
                val gson = Gson()
                val match = gson.fromJson(matchJson, Match::class.java)
                val matchFormated = Match(
                    id = currentMatchId,
                    user_id = match.user_id,
                    date = match.date,
                    ini_time = match.ini_time,
                    end_time = match.end_time,
                    total_time = match.total_time,
                    away_corner_location = match.away_corner_location,
                    home_corner_location = match.home_corner_location,
                    kickoff_location = match.kickoff_location,
                    isExample = match.isExample
                )

                val userId = loginUserId
                matchFormated.user_id = userId.toString()
                // Insert the match and wait for completion:
                withContext(Dispatchers.Main) { // Switch to Main thread for database operation
                    matchViewModel.insertMatch(match)
                }
                // Now that the insert is complete, get the new match ID:
                matchViewModel.insertMatch(match)
                sharedViewModel.newMatchId.postValue(currentMatchId)
                Log.d(TAG, "Match data saved to database with ID: $currentMatchId")
                // Send the broadcast
                sendDataTransferCompleteBroadcast()
                sharedViewModel.matchData.postValue("Match data received")
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
                sharedViewModel.locationData.postValue("Location data received")
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
        sharedViewModel.finished.postValue(true)
        sharedViewModel.serviceStage.postValue(4)
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