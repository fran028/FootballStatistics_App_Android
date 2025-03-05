package com.example.footballstatistics_app_android

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.Location
import com.example.footballstatistics_app_android.data.Match
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.util.UUID


class DataTransferService : Service() {
    private lateinit var database: AppDatabase
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var bluetoothServerSocket: BluetoothServerSocket? = null
    private var bluetoothSocket: BluetoothSocket? = null

    companion object {
        const val CHANNEL_ID = "DataChannel"
        const val NOTIFICATION_ID = 1
        private val MY_UUID: UUID = UUID.fromString("YOUR_UNIQUE_UUID") // Same UUID as smartwatch

        fun startService(context: Context) {
            val intent = Intent(context, DataTransferService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, DataTransferService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        startBluetoothServer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        closeBluetoothConnection()
    }

    private fun startBluetoothServer() {
        coroutineScope.launch {
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                Log.e("PhoneApp", "Bluetooth not supported")
                return@launch
            }
            if (ActivityCompat.checkSelfPermission(
                    this@DataTransferService,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("PhoneApp", "Bluetooth permission not granted")
                return@launch
            }
            bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("DataReceiver", MY_UUID)
            Log.d("PhoneApp", "Bluetooth server started, listening for connections")
            bluetoothSocket = bluetoothServerSocket?.accept()
            if (bluetoothSocket != null) {
                Log.d("PhoneApp", "Bluetooth connection accepted")
                receiveData()
            } else {
                Log.e("PhoneApp", "Bluetooth connection failed")
            }
        }
    }

    private fun receiveData() {
        coroutineScope.launch {
            try {
                val inputStream = bluetoothSocket?.inputStream
                val buffer = ByteArray(1024) // Adjust buffer size as needed
                var bytes: Int
                while (true) {
                    bytes = inputStream?.read(buffer) ?: -1
                    if (bytes == -1) {
                        Log.d("PhoneApp", "Bluetooth connection closed")
                        break
                    }
                    val receivedData = buffer.copyOfRange(0, bytes)
                    val (matchData, locationData) = deserializeData(receivedData)
                    storeData(matchData, locationData)
                }
            } catch (e: IOException) {
                Log.e("PhoneApp", "Error receiving data", e)
            } finally {
                closeBluetoothConnection()
            }
        }
    }

    private fun deserializeData(serializedData: ByteArray): Pair<List<Match>, List<Location>> {
        val byteArrayInputStream = ByteArrayInputStream(serializedData)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        val matchData = objectInputStream.readObject() as List<Match>
        val locationData = objectInputStream.readObject() as List<Location>
        objectInputStream.close()
        return Pair(matchData, locationData)
    }

    private suspend fun storeData(matchData: List<Match>, locationData: List<Location>) =
        withContext(Dispatchers.IO) {
        matchData.forEach { match ->
            database.matchDao().insertMatch(match)
            Log.d("PhoneApp", "Received match: $match")
        }
        locationData.forEach { location ->
            database.locationDao().insertLocation(location)
            Log.d("PhoneApp", "Received location: $location")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Data Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Data Service")
            .setContentText("Receiving data from smartwatch")
            .setSmallIcon(R.mipmap.ic_launcher) // Use a standard Android icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun closeBluetoothConnection() {
        try {
            bluetoothSocket?.close()
            bluetoothServerSocket?.close()
        } catch (e: IOException) {
            Log.e("PhoneApp", "Error closing Bluetooth connection", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}