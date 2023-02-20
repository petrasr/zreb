package com.example.card.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.card.data.model.ScratchCardState
import com.example.card.repository.CardRepository
import com.example.card.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScratchViewModelTest {

    @Test
    fun codeSuccess() = runTest {
        val cardRepository = FakeCardRepository()
        val viewModel = ScratchViewModel(cardRepository)
        val fakeCode = "fake_code"
        cardRepository.emitCard(fakeCode)
        val first = viewModel.code.first()
        assertEquals(fakeCode, first.data)
    }

    @Test
    fun generateCodeSuccess() = runTest {
        val cardRepository = FakeCardRepository()
        val viewModel = ScratchViewModel(cardRepository)
        val newCode = "new_code"
        viewModel.generateCode()
        cardRepository.emitNewCode(newCode)
        val first = viewModel.code.first()
        assertEquals(newCode, first.data)
    }

    @Test
    fun generateCodeFailure() = runTest {
        val cardRepository = FakeErrorCardRepository()
        val viewModel = ScratchViewModel(cardRepository)
        val newCode = "new_code"
        viewModel.generateCode()
        cardRepository.emitNewCode(newCode)
        val first = viewModel.code.first()
        assertEquals(Resource.error<String>("").status, first.status)
    }

    private class FakeCardRepository : CardRepository {
        private val cardFlow = MutableSharedFlow<String?>()
        private val newCodeFlow = MutableSharedFlow<String>()


        suspend fun emitCard(value: String?) = cardFlow.emit(value)

        suspend fun emitNewCode(value: String) = newCodeFlow.emit(value)

        override fun getCardState(): Flow<ScratchCardState> = emptyFlow()

        override fun setCardState(state: ScratchCardState) {
        }

        override fun getCardCode(): Flow<String?> = cardFlow

        override fun getNewCode(): Flow<String> = newCodeFlow

        override fun setNewCode(code: String) {
        }

    }

    private class FakeErrorCardRepository : CardRepository {
        private val newCodeFlow = MutableSharedFlow<String>()

        suspend fun emitNewCode(value: String) = newCodeFlow.emit(value)

        override fun getCardState(): Flow<ScratchCardState> = emptyFlow()

        override fun setCardState(state: ScratchCardState) {
        }

        override fun getCardCode(): Flow<String?> = emptyFlow()

        override fun getNewCode(): Flow<String> = flow {
            throw Exception("exception")
        }

        override fun setNewCode(code: String) {
        }

    }
}