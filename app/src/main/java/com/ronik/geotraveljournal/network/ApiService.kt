package com.ronik.geotraveljournal.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.Objects

data class RegisterRequest(val email: String, val password: String)

data class LoginRequest(val email: String, val password: String)

data class AuthResult(
    val id: Long,
    val email: String,
    val token: String,
    val createdAt: String
)

data class RegisterResponse(
    val status: Boolean,
    val result: AuthResult
)

data class LoginResponse(
    val status: Boolean,
    val result: AuthResult
)


data class JournalPreviewEntry(val id: Long, val title: String, val createdAt: String)
data class JournalUpdateEntry(val id: Long, val title: String, val description: String)
data class JournalDetailEntry(
    val id: Long, val title: String, val description: String, val distance: Double,
    val route: List<Objects>, val createdAt: String, val updatedAt: String
)


interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/journal")
    suspend fun getJournalPreview(
        @Header("Authorization") token: String
    ): List<JournalPreviewEntry>

    @GET("api/journal/{id}")
    suspend fun getDetailJournal(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
    ): JournalDetailEntry

    @PUT("api/journal/{id}")
    suspend fun updateJournalEntry(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body updatedEntry: JournalUpdateEntry
    ): JournalPreviewEntry

    @DELETE("api/journal/{id}")
    suspend fun deleteJournalEntry(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    )
}