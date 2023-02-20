package com.example.card.repository

import com.example.card.data.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ApiRepositoryImpl(private val apiService: ApiService) : ApiRepository {

    companion object {
        private const val VERSION_THRESHOLD = 80000
    }

    override suspend fun isScratchCardActivated(code: String): Flow<Boolean> = flow {
        val version = apiService.getVersion(code).android.toIntOrNull() ?: 0
        delay(4000)
        emit(version > VERSION_THRESHOLD)
    }.flowOn(Dispatchers.IO)
}