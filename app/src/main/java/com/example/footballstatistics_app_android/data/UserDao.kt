package com.example.footballstatistics_app_android.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: String): Flow<User>

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users")
    fun getUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE password = :password and username = :username")
    fun loginUser(username: String, password: String): Flow<List<User>>
}