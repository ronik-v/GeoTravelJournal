package com.ronik.geotraveljournal.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val context: Context,
    private val tokenProvider: () -> String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = tokenProvider.invoke()
        return if (!token.isNullOrEmpty()) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}