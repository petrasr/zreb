package com.example.card.repository

import com.example.card.data.model.ScratchCardState
import com.example.card.data.preferences.CardDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*

class CardRepositoryImpl(private val cardPreferences: CardDataStore) : CardRepository {

    override fun getCardState(): Flow<ScratchCardState> = cardPreferences.getCardState()
    .flowOn(Dispatchers.IO)

    override fun setCardState(state: ScratchCardState) {
        cardPreferences.setCardState(state)
    }

    override fun getCardCode(): Flow<String?> = flow {
        emit(cardPreferences.cardCode)
    }.flowOn(Dispatchers.IO)

    override fun getNewCode(): Flow<String> = flow {
        delay(2000)
        emit(UUID.randomUUID().toString())
    }.flowOn(Dispatchers.IO)

    override fun setNewCode(code: String) {
        cardPreferences.cardCode = code
    }
}