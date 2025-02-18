package com.example.footballstatistics_app_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatistics_app_android.data.User
import com.example.footballstatistics_app_android.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun insertUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }

    suspend fun checkUsernameExists(username: String): Boolean {
        return withContext(Dispatchers.IO) {
            val user = userRepository.getUserByUsername(username)
            user != null
        }
    }

    fun updateLoginStatus(userId: String) {
        viewModelScope.launch {
            userRepository.updateLoginStatus(userId)
        }
    }

    fun getLoginUser(): User? {
        return userRepository.getLoginUser()
    }

    fun getUserByUsername(): User?{
        return userRepository.getLoginUser()

    }
}