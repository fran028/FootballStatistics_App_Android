package com.example.footballstatistics_app_android.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.temporal.TemporalAmount

@Dao
interface MatchDao {
    @Insert
    suspend fun insertMatch(match: Match)

    @Update
    suspend fun updateMatch(match: Match)

    @Query("SELECT * FROM matchs")
    fun getAllMatches(): Flow<List<Match>>

    @Query("SELECT * FROM matchs ORDER BY date DESC LIMIT :amount")
    fun getLastMatches(amount: Int): Flow<List<Match>>


    @Query("SELECT * FROM matchs WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getMatchesBetweenDates(startDate: String, endDate: String): Flow<List<Match>>

    @Query("SELECT * FROM matchs WHERE id = :matchId")
    suspend fun getMatchById(matchId: String): Match?
}