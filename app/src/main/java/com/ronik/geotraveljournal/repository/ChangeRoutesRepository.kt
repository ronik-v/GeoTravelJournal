package com.ronik.geotraveljournal.repository

import android.content.Context
import com.ronik.geotraveljournal.data.TokenManager
import com.ronik.geotraveljournal.network.ApiResponse
import com.ronik.geotraveljournal.network.ApiService
import com.ronik.geotraveljournal.network.RouteForChange
import com.ronik.geotraveljournal.network.RoutesForChange
import kotlinx.coroutines.flow.first

class ChangeRoutesRepository(
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

    suspend fun changeRoute(routes: RoutesForChange): RouteForChange {
        val response = api.changeRoute(getTokenHeader(), routes)
        return checkResponse(response)
    }
}