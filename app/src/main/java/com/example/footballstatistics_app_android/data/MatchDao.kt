package com.example.footballstatistics_app_android.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match)

    @Update
    suspend fun updateMatch(match: Match)

    @Delete
    suspend fun deleteMatch(match: Match)

    @Query("SELECT * FROM matchs")
    fun getAllMatches(): Flow<List<Match>>

    @Query("SELECT * FROM matchs ORDER BY date DESC LIMIT :amount")
    fun getLastMatches(amount: Int): List<Match>


    @Query("SELECT * FROM matchs WHERE (date BETWEEN :startDate AND :endDate) AND user_id = :userId ORDER BY date DESC")
    fun getMatchesBetweenDates(startDate: String, endDate: String, userId: String): List<Match>

    @Query("SELECT * FROM matchs WHERE id = :matchId")
    suspend fun getMatchById(matchId: String): Match?

    @Query("SELECT COUNT(*) FROM matchs WHERE user_id = :userId")
    fun getMatchCount(userId: String): Int

    @Query("SELECT PRINTF('%02d:%02d:%02d', totalSeconds / 3600, (totalSeconds % 3600) / 60, totalSeconds % 60) AS totalDuration FROM (SELECT SUM( CAST(SUBSTR(total_time, 1, 2) AS INTEGER) * 3600 + CAST(SUBSTR(total_time, 4, 2) AS INTEGER) * 60 + CAST(SUBSTR(total_time, 7, 2) AS INTEGER) ) AS totalSeconds FROM matchs)")
    fun getTotalDuration(): String


}