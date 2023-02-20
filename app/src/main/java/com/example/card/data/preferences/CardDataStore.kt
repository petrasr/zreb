package com.example.card.data.preferences

import com.example.card.data.model.ScratchCardState
import kotlinx.coroutines.flow.Flow

interface CardDataStore {
    var cardCode: String?

    fun getCardState(): Flow<ScratchCardState>

    fun setCardState(state: ScratchCardState)
}