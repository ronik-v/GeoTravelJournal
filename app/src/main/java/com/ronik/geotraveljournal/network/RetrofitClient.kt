package com.ronik.geotraveljournal.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.ronik.geotraveljournal.Config
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    @Volatile
    private var INSTANCE: ApiService? = null

    fun getApiService(
        context: Context,
        tokenProvider: () -> String?
    ): ApiService {
        return INSTANCE ?: synchronized(this) {
            val gson = GsonBuilder().setLenient().create()

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context, tokenProvider))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(Config.apiBaseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            val apiService = retrofit.create(ApiService::class.java)
            INSTANCE = apiService
            apiService
        }
    }
}