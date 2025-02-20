package com.example.footballstatistics_app_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatistics_app_android.data.User
import com.example.footballstatistics_app_android.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _loginUser = MutableStateFlow<User?>(null)
    val loginUser: StateFlow<User?> = _loginUser

    fun getLoginUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.getLoginUser()
            withContext(Dispatchers.Main) {
                _loginUser.value = user
            }
        }
    }


    suspend fun getUserByUsername(username: String): User? {
        return withContext(Dispatchers.IO) {
            userRepository.getUserByUsername(username)
        }
    }

}