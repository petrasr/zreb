package com.example.card.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.card.App
import com.example.card.data.model.ScratchCardState
import com.example.card.data.workers.ActivateWorker
import com.example.card.repository.CardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

class ActivateViewModel(private val cardRepository: CardRepository) : ViewModel() {

    val buttonEnable: StateFlow<Boolean> =
        cardRepository.getCardState()
            .map { it == ScratchCardState.SCRATCHED }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = false
            )

    fun activateCard() {
        viewModelScope.launch {
            cardRepository.getCardCode()
                .collect { code ->
                    if (!code.isNullOrBlank()) {
                        startWorker(code)
                    }
                }
        }
    }

    private fun startWorker(code: String) {
        WorkManager.getInstance(App.instance)
            .beginUniqueWork(
                ActivateWorker.ACTIVATE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                ActivateWorker.getWorker(code)
            ).enqueue()
    }
}

@Suppress("UNCHECKED_CAST")
class ActivateVMF(private val cardRepository: CardRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ActivateViewModel(cardRepository) as T
}
