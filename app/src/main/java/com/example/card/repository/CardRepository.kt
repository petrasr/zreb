package com.example.card.repository

import com.example.card.data.model.ScratchCardState
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun getCardState(): Flow<ScratchCardState>

    fun setCardState(state: ScratchCardState)

    fun getCardCode(): Flow<String?>

    fun getNewCode(): Flow<String>

    fun setNewCode(code: String)
}