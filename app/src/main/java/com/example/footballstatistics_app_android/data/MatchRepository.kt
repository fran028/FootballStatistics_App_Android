package com.example.footballstatistics_app_android.data

class MatchRepository (private val matchDao: MatchDao) {
    suspend fun insertMatch(match: Match) {
        matchDao.insertMatch(match)
    }


}