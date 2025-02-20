package com.ronik.geotraveljournal.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

data class ApiResponse<T>(
    val status: Boolean,
    val result: T
)

data class JournalCreateEntry(
    val title: String,
    val description: String,
    val distance: Double,
    val route: List<Map<String, String>>
)

data class JournalUpdateEntry(
    val title: String,
    val description: String,
    val distance: Double,
    val route: List<Map<String, String>>
)

data class JournalPreviewEntry(
    val id: Long,
    val title: String,
    val createdAt: String
)

data class JournalDetailEntry(
    val id: Long,
    val title: String,
    val description: String,
    val distance: Double,
    val route: List<Map<String, String>>,
    val createdAt: String,
    val updatedAt: String
)

data class Point(
    val latitude: Double,
    val longitude: Double
)

data class RoutesForChange(
    val routes: Map<String, List<Point>>,
    val userCoordinates: Point
)

data class RouteForChange(
    val route: String
)

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/journal")
    suspend fun saveJournal(
        @Header("Authorization") token: String,
        @Body createEntry: JournalCreateEntry
    ): ApiResponse<JournalDetailEntry>

    @GET("api/journal/history")
    suspend fun getJournalPreview(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<List<JournalPreviewEntry>>

    @GET("api/journal/{id}")
    suspend fun getDetailJournal(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<JournalDetailEntry>

    @PUT("api/journal/{id}")
    suspend fun updateJournalEntry(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body updatedEntry: JournalUpdateEntry
    ): ApiResponse<JournalDetailEntry>

    @DELETE("api/journal/{id}")
    suspend fun deleteJournalEntry(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<String>

    @DELETE("api/journal/history")
    suspend fun clearHistory(
        @Header("Authorization") token: String
    ): ApiResponse<String>

    @POST("api/change-route")
    suspend fun changeRoute(
        @Header("Authorization") token: String,
        @Body routes: RoutesForChange
    ): ApiResponse<RouteForChange>
}