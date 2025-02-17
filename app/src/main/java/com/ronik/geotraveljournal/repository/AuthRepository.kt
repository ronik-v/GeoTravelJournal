package com.ronik.geotraveljournal.repository

import android.content.Context
import com.ronik.geotraveljournal.data.TokenManager
import com.ronik.geotraveljournal.network.ApiService
import com.ronik.geotraveljournal.network.LoginRequest
import com.ronik.geotraveljournal.network.RegisterRequest

class AuthRepository(
    private val context: Context,
    private val api: ApiService
) {
    suspend fun register(email: String, password: String): Boolean {
        val response = api.register(RegisterRequest(email, password))
        if (response.status) {
            TokenManager.saveToken(context, response.result.token)
            return true
        }
        return false
    }

    suspend fun login(email: String, password: String): Boolean {
        val response = api.login(LoginRequest(email, password))
        if (response.status) {
            TokenManager.saveToken(context, response.result.token)
            return true
        }
        return false
    }
}
