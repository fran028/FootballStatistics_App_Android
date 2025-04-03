package com.example.footballstatistics_app_android.data

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

    suspend fun getLastMatchId(userId: String): Int {
        return matchDao.getLastMatchId(userId)
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

    fun getTotalDuration(id: String): String {
        return matchDao.getTotalDuration(id)
    }

    suspend fun getExampleMatch(id: String): Match? {
        return matchDao.getExampleMatch(id)
    }

    suspend fun getAllUserMatches(id: String): List<Match> {
        return matchDao.getAllUserMatches(id)
    }


}

