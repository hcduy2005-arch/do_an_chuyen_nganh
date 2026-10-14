package com.example.doanckapi.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doanckapi.data.repository.PredictionRepository

import androidx.lifecycle.viewModelScope
import com.example.doanckapi.data.db.HistoryEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class HistoryViewModel(val repository: PredictionRepository) : ViewModel() {
    private val userIdFlow = MutableStateFlow(-1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val historyFlow = userIdFlow.flatMapLatest { id -> repository.getAllHistory(id) }

    fun setUserId(id: Int) {
        userIdFlow.value = id
    }

    fun deleteHistory(history: HistoryEntity) {
        viewModelScope.launch {
            repository.deleteHistory(history)
        }
    }
}

class HistoryViewModelFactory(private val repository: PredictionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
