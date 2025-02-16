package com.example.footballstatistics_app_android.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_name")
    val username: String,
    val password: String,
    val fullName: String,
    val dateOfBirth: String,
    val height: Int,
)
