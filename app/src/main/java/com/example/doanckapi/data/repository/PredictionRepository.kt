package com.example.doanckapi.data.repository

import com.example.doanckapi.data.api.ApiService
import com.example.doanckapi.data.db.HistoryDao
import com.example.doanckapi.data.db.HistoryEntity
import com.example.doanckapi.data.model.PredictionRequest

class PredictionRepository(
    private val apiService: ApiService,
    private val historyDao: HistoryDao
) {
    suspend fun predictDiabetes(request: PredictionRequest) = apiService.predictDiabetes(request)
    
    suspend fun saveHistory(history: HistoryEntity) = historyDao.insertHistory(history)
    
    fun getAllHistory(userId: Int) = historyDao.getAllHistory(userId)
    
    suspend fun clearHistory(userId: Int) = historyDao.clearHistory(userId)
    
    suspend fun deleteHistory(history: HistoryEntity) = historyDao.deleteHistory(history)
}
