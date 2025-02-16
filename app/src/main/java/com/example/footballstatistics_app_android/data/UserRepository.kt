package com.example.footballstatistics_app_android.data

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
}