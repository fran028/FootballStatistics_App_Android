package com.example.footballstatistics_app_android.data

import kotlinx.coroutines.flow.Flow

class MatchRepository (private val matchDao: MatchDao) {
    suspend fun insertMatch(match: Match) {
        matchDao.insertMatch(match)
    }

    suspend fun updateMatch(match: Match) {
        matchDao.updateMatch(match)
    }

    fun getMatchById(matchId: String): Match? {
        return matchDao.getMatchById(matchId)
    }

    fun getAllMatches(): Flow<List<Match>> {
        return matchDao.getAllMatches()

    }

    fun getLastMatches(amount: Int): List<Match> {
        return matchDao.getLastMatches(amount)
    }

    fun getMatchesBetweenDates(startDate: String, endDate: String): Flow<List<Match>> {
        return matchDao.getMatchesBetweenDates(startDate, endDate)
    }

    suspend fun deleteMatch(match: Match) {
        matchDao.deleteMatch(match)
    }

    fun getMatchCount(): Int {
        return matchDao.getMatchCount()
    }

    fun getTotalDuration(): String {
        return matchDao.getTotalDuration()
    }


}