package com.example.footballstatistics_app_android.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatistics_app_android.data.User
import com.example.footballstatistics_app_android.data.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val userDao: UserDao) : ViewModel() {

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Initial)
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = userDao.getUserByUsernameAndPassword(username, password)
                if (user != null) {
                    withContext(Dispatchers.Main) {
                        _loginResult.value = LoginResult.Success(user) // Pass the user object here
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _loginResult.value = LoginResult.Error("Invalid username or password")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _loginResult.value = LoginResult.Error("An error occurred")
                }
            }
        }
    }
}

sealed class LoginResult {
    data object Initial : LoginResult()
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}