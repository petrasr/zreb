package com.example.card.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.card.data.model.ScratchCardState
import com.example.card.repository.CardRepository
import com.example.card.repository.CardRepositoryImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainViewModel(private val cardRepository: CardRepository) : ViewModel() {

    val state = cardRepository.getCardState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ScratchCardState.NEW
        )

    fun resetCard() {
        cardRepository.setCardState(ScratchCardState.NEW)
        cardRepository.setNewCode("")
    }
}

@Suppress("UNCHECKED_CAST")
class MainVMF(private val cardRepository: CardRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MainViewModel(cardRepository) as T
}