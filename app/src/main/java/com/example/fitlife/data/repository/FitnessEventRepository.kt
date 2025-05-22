package com.example.fitlife.data.repository

import android.app.Application
import com.example.fitlife.data.database.WorkoutDatabase // 确保这是你数据库类的正确名称
import com.example.fitlife.data.dao.FitnessEventDao
import com.example.fitlife.data.model.FitnessEvent
import kotlinx.coroutines.flow.Flow

class FitnessEventRepository(application: Application) {

    private val fitnessEventDao: FitnessEventDao =
        WorkoutDatabase.getDatabase(application).fitnessEventDao()

    fun getEventsForDay(dayStartMillis: Long, dayEndMillis: Long): Flow<List<FitnessEvent>> {
        return fitnessEventDao.getEventsForDay(dayStartMillis, dayEndMillis)
    }

    val allFitnessEvents: Flow<List<FitnessEvent>> = fitnessEventDao.getAllEvents()

    suspend fun insertEvent(event: FitnessEvent): Long {
        return fitnessEventDao.insertEvent(event)
    }

    suspend fun updateEvent(event: FitnessEvent) {
        fitnessEventDao.updateEvent(event)
    }

    suspend fun deleteEvent(event: FitnessEvent) {
        fitnessEventDao.deleteEvent(event)
    }

    suspend fun getEventById(id: Long): FitnessEvent? {
        return fitnessEventDao.getEventById(id)
    }
}