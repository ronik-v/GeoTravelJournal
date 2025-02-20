package com.ronik.geotraveljournal.activity.journal

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.ronik.geotraveljournal.data.TokenManager
import com.ronik.geotraveljournal.navigation.RouteHistoryNav
import com.ronik.geotraveljournal.network.RetrofitClient
import com.ronik.geotraveljournal.repository.JournalRepository
import com.ronik.geotraveljournal.viewmodel.JournalViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class RouteHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenProvider = { runBlocking { TokenManager.getTokenFlow(this@RouteHistoryActivity).first() } }
        val apiService = RetrofitClient.getApiService(this, tokenProvider)
        val repository = JournalRepository(this, apiService)
        val viewModel = JournalViewModel(repository)

        setContent {
            RouteHistoryNav(viewModel = viewModel)
        }
    }
}
