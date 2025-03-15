package com.example.footballstatistics_app_android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DataTransferReceiver(private val onDataTransferComplete: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DataTransferReceiver", "onReceive called")
        if (intent.action == Constants.DATA_TRANSFER_COMPLETE) {
            Log.d("DataTransferReceiver", "Data transfer completed")
            onDataTransferComplete()
        }
    }
}