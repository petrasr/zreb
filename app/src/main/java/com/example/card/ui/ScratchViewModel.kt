package com.example.card.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.card.data.model.ScratchCardState
import com.example.card.repository.CardRepository
import com.example.card.repository.CardRepositoryImpl
import com.example.card.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ScratchViewModel(private val cardRepository: CardRepository) : ViewModel() {

    private val _code = MutableStateFlow<Resource<String>>(Resource.loading())
    val code: StateFlow<Resource<String>> = _code

    init {
        initCode()
    }

    private fun initCode() {
        viewModelScope.launch {
            cardRepository.getCardCode()
                .collect {
                    _code.value = Resource.success(it ?: "")
                }
        }
    }

    fun generateCode() {
        viewModelScope.launch {
            _code.value = Resource.loading()
            cardRepository.getNewCode()
                .catch { e ->
                    _code.value = Resource.error(e.toString())
                }
                .collect { code ->
                    _code.value = Resource.success(code)
                    cardRepository.setNewCode(code)
                    cardRepository.setCardState(ScratchCardState.SCRATCHED)
                }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class ScratchVMF(private val cardRepository: CardRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ScratchViewModel(cardRepository) as T
}
