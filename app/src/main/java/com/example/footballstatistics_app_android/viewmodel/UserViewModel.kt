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

    fun updateLoginStatus(userId: Int) {
        viewModelScope.launch {
            userRepository.updateLoginStatus(userId)
        }
    }

    fun logOutUsers() {
        viewModelScope.launch {
            userRepository.logOutUsers()
        }
    }

    private val _loginUser = MutableStateFlow<User?>(null)
    val loginUser: StateFlow<User?> = _loginUser

    fun getLoginUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _loginUser.value = userRepository.getLoginUser()
        }
    }


    suspend fun getUserByUsername(username: String): User? {
        return withContext(Dispatchers.IO) {
            userRepository.getUserByUsername(username)
        }
    }

}