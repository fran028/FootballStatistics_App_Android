package com.example.footballstatistics_app_android.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

class MatchRepository (private val matchDao: MatchDao) {

    suspend fun insertMatch(match: Match) {
        matchDao.insertMatch(match)
    }

    suspend fun updateMatch(match: Match) {
        matchDao.updateMatch(match)
    }

    suspend fun getMatchById(matchId: Int): Match? {
        return matchDao.getMatchById(matchId)
    }

    fun getAllMatches(): Flow<List<Match>> {
        return matchDao.getAllMatches()

    }

    fun getLastMatches(amount: Int, userId: String): List<Match> {
        return matchDao.getLastMatches(amount, userId)
    }

    fun getMatchesBetweenDates(startDate: String, endDate: String, userId: String): List<Match> {
        return matchDao.getMatchesBetweenDates(startDate, endDate, userId)
    }

    suspend fun deleteMatch(match: Match) {
        matchDao.deleteMatch(match)
    }

    fun getMatchCount(userId: String): Int {
        return matchDao.getMatchCount(userId = userId)
    }

    fun getTotalDuration(): String {
        return matchDao.getTotalDuration()
    }

    suspend fun getExampleMatch(id: String): Match? {
        return matchDao.getExampleMatch(id)
    }


}

