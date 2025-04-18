package com.example.footballstatistics_app_android.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location)

    @Update
    suspend fun updateLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Query("SELECT * FROM locations")
    fun getAllLocations(): Flow<List<Location>>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocationById(id: String): Location?

    @Query("SELECT * FROM locations WHERE match_id = :id ORDER BY timestamp ASC")
    suspend fun getLocationsByMatchId(id: String): List<Location>?

    suspend fun checkIfMatchHasLocation(id: String): Boolean {
        return (getLocationsByMatchId(id) != null)
    }

    @Insert
    suspend fun insertAllLocations(location: List<Location>)

}