package com.example.footballstatistics_app_android.viewmodel

import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatistics_app_android.data.User
import com.example.footballstatistics_app_android.data.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val userDao: UserDao) : ViewModel() {

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Initial)
    val loginResult: StateFlow<LoginResult> = _loginResult.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            userDao.loginUser(username, password).collect { users ->
                if (users.isNotEmpty()) {
                    _loginResult.value = LoginResult.Success(users.first())
                } else {
                    _loginResult.value = LoginResult.Error("Invalid username or password")
                }
            }
        }
    }
}

sealed class LoginResult {
    object Initial : LoginResult()
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}