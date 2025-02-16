package com.example.footballstatistics_app_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatistics_app_android.data.User
import com.example.footballstatistics_app_android.data.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun insertUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }

    suspend fun checkUsernameExists(username: String): Boolean {
        return userRepository.getUserByUsername(username) != null
    }
}