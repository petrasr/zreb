package com.example.card.repository

import kotlinx.coroutines.flow.Flow

interface ApiRepository {

    suspend fun isScratchCardActivated(code: String): Flow<Boolean>
}