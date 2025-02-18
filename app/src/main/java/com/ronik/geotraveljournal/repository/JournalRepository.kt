package com.ronik.geotraveljournal.repository

import android.content.Context
import com.ronik.geotraveljournal.data.TokenManager
import com.ronik.geotraveljournal.network.ApiResponse
import com.ronik.geotraveljournal.network.ApiService
import com.ronik.geotraveljournal.network.JournalCreateEntry
import com.ronik.geotraveljournal.network.JournalDetailEntry
import com.ronik.geotraveljournal.network.JournalPreviewEntry
import com.ronik.geotraveljournal.network.JournalUpdateEntry
import kotlinx.coroutines.flow.first

class JournalRepository(
    private val context: Context,
    private val api: ApiService
) {
    private suspend fun getTokenHeader(): String {
        val token = TokenManager.getTokenFlow(context).first()
        if (token.isNullOrEmpty()) {
            throw Exception("Токен отсутствует. Пользователь не авторизован.")
        }
        return "Bearer $token"
    }

    private fun <T> checkResponse(response: ApiResponse<T>): T {
        return if (response.status) {
            response.result
        } else {
            throw Exception("Ошибка обращения к API")
        }
    }

    suspend fun addJournal(createEntry: JournalCreateEntry): JournalDetailEntry {
        val response = api.saveJournal(getTokenHeader(), createEntry)
        return checkResponse(response)
    }

    suspend fun getJournalPreview(page: Int = 0, size: Int = 10): List<JournalPreviewEntry> {
        val response = api.getJournalPreview(getTokenHeader(), page, size)
        return checkResponse(response)
    }

    suspend fun getJournalDetail(id: Long): JournalDetailEntry {
        val response = api.getDetailJournal(getTokenHeader(), id)
        return checkResponse(response)
    }

    suspend fun updateJournal(id: Long, updatedEntry: JournalUpdateEntry): JournalDetailEntry {
        val response = api.updateJournalEntry(getTokenHeader(), id, updatedEntry)
        return checkResponse(response)
    }

    suspend fun deleteJournal(id: Long) {
        val response = api.deleteJournalEntry(getTokenHeader(), id)
        checkResponse(response)
    }

    suspend fun clearHistory() {
        val response = api.clearHistory(getTokenHeader())
        checkResponse(response)
    }
}
