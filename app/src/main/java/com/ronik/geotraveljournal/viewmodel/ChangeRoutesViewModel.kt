package com.ronik.geotraveljournal.viewmodel

import androidx.lifecycle.ViewModel
import com.ronik.geotraveljournal.network.RouteForChange
import com.ronik.geotraveljournal.network.RoutesForChange
import com.ronik.geotraveljournal.repository.ChangeRoutesRepository

class ChangeRoutesViewModel(
    private val repository: ChangeRoutesRepository
): ViewModel() {

    suspend fun changeRoute(routes: RoutesForChange): RouteForChange = repository.changeRoute(routes)
}