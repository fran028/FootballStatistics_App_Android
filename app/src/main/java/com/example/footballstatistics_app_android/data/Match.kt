package com.example.footballstatistics_app_android.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matchs")
data class Match(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "user_id") var user_id: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "ini_time") val ini_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
    @ColumnInfo(name = "total_time") val total_time: String,
    @ColumnInfo(name = "away_corner_location") val away_corner_location: String,
    @ColumnInfo(name = "home_corner_location") val home_corner_location: String,
    @ColumnInfo(name = "kickoff_location") val kickoff_location: String,
    @ColumnInfo(name = "isExample") val isExample: Boolean
)