package com.example.card.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.example.card.data.model.ScratchCardState
import com.example.card.data.workers.ActivateWorker
import com.example.card.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivateViewModelTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun buttonEnabledSuccess() = runTest {
        val cardRepository = FakeCardRepository()
        val viewModel = ActivateViewModel(cardRepository)

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.buttonEnable.collect()
        }

        assertFalse(viewModel.buttonEnable.value)
        cardRepository.emitState(ScratchCardState.SCRATCHED)
        val second = viewModel.buttonEnable.value
        assertTrue(second)
        collectJob.cancel()
    }

    @Test
    fun activateCardSuccess() = runTest {
        val worker = TestListenableWorkerBuilder<ActivateWorker>(
            context = context,
            inputData = workDataOf("key_code" to "1234")
        ).build()

        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun activateCardFailed() = runTest {
        val worker = TestListenableWorkerBuilder<ActivateWorker>(
            context = context
        ).build()

        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.failure(), result)
    }

    private class FakeCardRepository : CardRepository {
        private val stateFlow = MutableSharedFlow<ScratchCardState>()

        suspend fun emitState(value: ScratchCardState) = stateFlow.emit(value)

        override fun getCardState(): Flow<ScratchCardState> = stateFlow

        override fun setCardState(state: ScratchCardState) {
        }

        override fun getCardCode(): Flow<String?> = emptyFlow()

        override fun getNewCode(): Flow<String> = emptyFlow()

        override fun setNewCode(code: String) {
        }

    }
}