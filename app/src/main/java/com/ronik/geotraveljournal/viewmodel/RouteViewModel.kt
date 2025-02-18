package com.ronik.geotraveljournal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ronik.geotraveljournal.network.JournalCreateEntry
import com.ronik.geotraveljournal.network.JournalDetailEntry
import com.ronik.geotraveljournal.network.JournalPreviewEntry
import com.ronik.geotraveljournal.network.JournalUpdateEntry
import com.ronik.geotraveljournal.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RouteViewModel(
    private val repository: JournalRepository
) : ViewModel() {

    private val _routes = MutableStateFlow<List<JournalPreviewEntry>>(emptyList())
    val routes: StateFlow<List<JournalPreviewEntry>> = _routes

    private val _routeDetail = MutableStateFlow<JournalDetailEntry?>(null)
    val routeDetail: StateFlow<JournalDetailEntry?> = _routeDetail

    suspend fun fetchRoutes(page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            try {
                _routes.value = repository.getJournalPreview(page, size)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addJournal(createEntry: JournalCreateEntry): JournalDetailEntry {
        return repository.addJournal(createEntry)
    }

    fun fetchRouteDetail(id: Long) {
        viewModelScope.launch {
            try {
                _routeDetail.value = repository.getJournalDetail(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateRoute(id: Long, updatedEntry: JournalUpdateEntry) {
        viewModelScope.launch {
            try {
                _routeDetail.value = repository.updateJournal(id, updatedEntry)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteRoute(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteJournal(id)
                fetchRoutes()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                repository.clearHistory()
                _routes.value = emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
