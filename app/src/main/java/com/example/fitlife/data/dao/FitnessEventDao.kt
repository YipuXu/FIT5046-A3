package com.example.fitlife.data.dao

import androidx.room.*
import com.example.fitlife.data.model.FitnessEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: FitnessEvent): Long

    @Update
    suspend fun updateEvent(event: FitnessEvent)

    @Delete
    suspend fun deleteEvent(event: FitnessEvent)

    @Query("SELECT * FROM fitness_events ORDER BY startTime DESC")
    fun getAllEvents(): Flow<List<FitnessEvent>>

    @Query("SELECT * FROM fitness_events WHERE id = :id")
    suspend fun getEventById(id: Long): FitnessEvent?

    @Query("SELECT * FROM fitness_events WHERE startTime >= :dayStartMillis AND startTime < :dayEndMillis ORDER BY startTime ASC")
    fun getEventsForDay(dayStartMillis: Long, dayEndMillis: Long): Flow<List<FitnessEvent>>
}