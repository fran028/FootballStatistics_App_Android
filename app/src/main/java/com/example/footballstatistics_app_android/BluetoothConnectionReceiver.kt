package com.example.footballstatistics_app_android

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


class BluetoothConnectionReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        val device: BluetoothDevice? =
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        val action: String? = intent.action
        when {
            BluetoothDevice.ACTION_ACL_CONNECTED == action -> {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d(
                        "Bluetooth",
                        "Bluetooth connected with ${device?.name}  address: ${device?.address}"
                    )
                }else{
                    Log.d("Bluetooth", "Missing Bluetooth permission")
                }
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED == action -> {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("Bluetooth", "Bluetooth disconnected with ${device?.name}")
                }else{
                    Log.d("Bluetooth", "Missing Bluetooth permission")
                }
            }

            else -> {
                Log.d("Bluetooth", "Action: ${action}")
            }
        }
    }
}