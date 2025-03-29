package com.example.footballstatistics_app_android.pages

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.footballstatistics_app_android.DataListenerService
import android.Manifest
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Screen
import com.example.footballstatistics_app_android.Theme.LeagueGothic
import com.example.footballstatistics_app_android.Theme.RobotoCondensed
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.gray
import com.example.footballstatistics_app_android.Theme.green
import com.example.footballstatistics_app_android.Theme.red
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.components.ButtonIconObject
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.components.ViewTitle
import com.example.footballstatistics_app_android.viewmodel.SharedDataViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddMatchPage(modifier: Modifier = Modifier, navController: NavController) {
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    val sharedViewModel: SharedDataViewModel = viewModel()

    val serviceState by sharedViewModel.serviceState.observeAsState()
    val matchData by sharedViewModel.matchData.observeAsState()
    val locationData by sharedViewModel.locationData.observeAsState()
    val newMatchId by sharedViewModel.newMatchId.observeAsState()

    LaunchedEffect(serviceState) {
        Log.d("AddMatchPage", "serviceState changed: $serviceState")
    }

    var isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }
    var isConnectedToWatch by remember { mutableStateOf(false) }
    var isServiceRunning by remember { mutableStateOf(false) } // Track service state

    // Check Bluetooth on initial composition
    LaunchedEffect(Unit) {
        isBluetoothEnabled = bluetoothAdapter?.isEnabled == true

        if (isBluetoothEnabled) {
            checkConnectedDevice(context, bluetoothAdapter, { connected ->
                isConnectedToWatch = connected
            })
        }
    }

    // Handle Bluetooth enabling if it's off
    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
        if (bluetoothAdapter?.isEnabled == true) {
            checkConnectedDevice(context, bluetoothAdapter, { connected ->
                isConnectedToWatch = connected
            })
        }
    }
    LaunchedEffect(isBluetoothEnabled) {
        if (!isBluetoothEnabled) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBluetoothIntent)
            } else {
                Log.e("AddMatchPage", "Bluetooth permission not granted")
                // Handle the case where Bluetooth permission is not granted (e.g., display a message)
            }
        } else {
            checkConnectedDevice(context, bluetoothAdapter, { connected ->
                isConnectedToWatch = connected
            })
        }
    }
    // Update connection status when Bluetooth is enabled
    LaunchedEffect(isBluetoothEnabled) {
        if (isBluetoothEnabled) {
            checkConnectedDevice(context, bluetoothAdapter, { connected ->
                isConnectedToWatch = connected
            })
        }
    }

    LaunchedEffect(Unit) {
        Log.d("AddMatchPage", "ViewModel hash: ${sharedViewModel.hashCode()}")
    }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .verticalScroll(scrollState) ,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ViewTitle(
            title = "ADD NEW MATCH",
            image = R.drawable.metegol,
            navController = navController
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(
                text = "CONNECTION",
                fontFamily = LeagueGothic,
                fontSize = 40.sp,
                color = white

            )
            Spacer(modifier = Modifier.height(24.dp))
            ButtonIconObject(
                text = "Bluetooth: ${if (isBluetoothEnabled) "Enabled" else "Disabled"}",
                bgcolor = if (isBluetoothEnabled) blue else white,
                height = 50.dp,
                textcolor = if (isBluetoothEnabled) black else gray,
                value = "",
                icon = R.drawable.bluetooth,
                onClick = { }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ButtonIconObject(
                text = "Connected to Watch: ${if (isConnectedToWatch) "Yes" else "No"}",
                bgcolor = if (isBluetoothEnabled) green else white,
                height = 50.dp,
                textcolor = if (isBluetoothEnabled) black else gray,
                value = "",
                icon = R.drawable.smartwatch,
                onClick = { }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "NEW MATCH",
                fontFamily = LeagueGothic,
                fontSize = 40.sp,
                color = white

            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isConnectedToWatch && !isServiceRunning) {
                ButtonIconObject(
                    text = "Add new match",
                    bgcolor = if (isBluetoothEnabled) green else white,
                    height = 50.dp,
                    textcolor = if (isBluetoothEnabled) black else gray,
                    value = "",
                    icon = R.drawable.smartwatch,
                    onClick = {
                        if (isBluetoothEnabled) {
                            Log.d("AddMatchPage", "Starting service")
                            startDataListenerService(context)
                            isServiceRunning = true
                        }
                    }
                )
            } else {
                Text(
                    text = "Service State: $serviceState",
                    fontFamily = RobotoCondensed,
                    fontSize = 24.sp,
                    color = white

                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Match Data: $matchData",
                    fontFamily = RobotoCondensed,
                    fontSize = 24.sp,
                    color = white
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Location Data: $locationData",
                    fontFamily = RobotoCondensed,
                    fontSize = 24.sp,
                    color = white
                )
                Spacer(modifier = Modifier.height(8.dp))

                ButtonObject(
                    text = "Cancel",
                    bgcolor = red,
                    textcolor = black,
                    width = 550.dp,
                    height = 60.dp,
                    onClick = {
                        if (isServiceRunning) {
                            Log.d("AddMatchPage", "Stopping service")
                            stopDataListenerService(context)
                            isServiceRunning = false
                        }
                    }
                )
            }

            if (newMatchId != -1) {

                val matchName = "Match ${newMatchId}"
                ButtonIconObject(
                    text = matchName,
                    onClick = { navController.navigate(Screen.Match.createRoute(newMatchId)) },
                    bgcolor = yellow,
                    height = 50.dp,
                    textcolor = black,
                    icon = R.drawable.soccer,
                    value = ""
                )
            }
        }
    }
}

private fun startDataListenerService(context: Context) {
    val serviceIntent = Intent(context, DataListenerService::class.java)
    context.startService(serviceIntent)
    Log.d("AddMatchPage", "Service started")
}

private fun stopDataListenerService(context: Context) {
    val serviceIntent = Intent(context, DataListenerService::class.java)
    context.stopService(serviceIntent)
    Log.d("AddMatchPage", "Service stopped")
}

private fun checkConnectedDevice(context: Context, bluetoothAdapter: BluetoothAdapter?, setConnectedToWatch: (Boolean) -> Unit) {
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
        setConnectedToWatch(false)
        return
    }

    val bondedDevices: Set<BluetoothDevice>? = if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
        bluetoothAdapter?.bondedDevices
    }else{
        Log.e("AddMatchPage", "Bluetooth permission not granted")
        setConnectedToWatch(false)
        return
    }

    // Iterate through bonded devices and check for your smartwatch (you'll need a way to identify it)
    val isWatchConnected = bondedDevices?.any {
        // Replace with your logic to identify your smartwatch. For example:
        it.name?.contains("Watch") == true || it.address == "YOUR_WATCH_BLUETOOTH_ADDRESS"
    } ?: false

    setConnectedToWatch(isWatchConnected)
}