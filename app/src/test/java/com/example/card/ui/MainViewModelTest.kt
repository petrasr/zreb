package com.example.card.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.card.data.model.ScratchCardState
import com.example.card.data.preferences.CardDataStore
import com.example.card.repository.CardRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    private val cardRepository = CardRepositoryImpl(FakeDataStore())
    private val viewModel = MainViewModel(cardRepository)

    @Test
    fun resetCardSuccess() {
        viewModel.resetCard()
        assertEquals(ScratchCardState.NEW, viewModel.state.value)
    }

    @Test
    fun resetCardFailure() {
        viewModel.resetCard()
        assertNotEquals(ScratchCardState.SCRATCHED, viewModel.state.value)
        assertNotEquals(ScratchCardState.ACTIVATED, viewModel.state.value)
    }

    @Test
    fun stateSuccess() = runTest {
        val second = viewModel.state.drop(1).first()
        assertEquals(ScratchCardState.SCRATCHED, second)
    }

    @Test
    fun stateFailure() = runTest {
        val second = viewModel.state.drop(1).first()
        assertNotEquals(ScratchCardState.ACTIVATED, second)
        assertNotEquals(ScratchCardState.NEW, second)
    }

    private class FakeDataStore : CardDataStore {
        override var cardCode: String?
            get() = "fake_code"
            set(value) {}

        override fun getCardState(): Flow<ScratchCardState> = flowOf(ScratchCardState.SCRATCHED)

        override fun setCardState(state: ScratchCardState) {
        }

    }
}