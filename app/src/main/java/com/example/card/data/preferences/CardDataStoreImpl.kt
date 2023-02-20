package com.example.card.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.card.App
import com.example.card.data.model.ScratchCardState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CardDataStoreImpl : CardDataStore {

    companion object {
        private const val PREFS_NAME = "card_preferences"
        private const val KEY_CARD_CODE = "card_code"
        private const val KEY_CARD_STATE = "card_state"
    }


    private val prefs: SharedPreferences =
        App.instance.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    override var cardCode: String?
        get() = prefs.getString(KEY_CARD_CODE, null)
        set(value) = prefs.edit().putString(KEY_CARD_CODE, value).apply()

   override fun getCardState(): Flow<ScratchCardState> = callbackFlow {
       trySend(prefs.getString(KEY_CARD_STATE, null).toCardState())
       val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
           if (key == KEY_CARD_STATE) {
               trySend(sharedPreferences.getString(KEY_CARD_STATE, null).toCardState())
           }
       }
       prefs.registerOnSharedPreferenceChangeListener(listener)
       awaitClose {
           prefs.unregisterOnSharedPreferenceChangeListener(listener)
       }
   }

    override fun setCardState(state: ScratchCardState) {
        prefs.edit().putString(KEY_CARD_STATE, state.name).apply()
    }

    private fun String?.toCardState(): ScratchCardState =
        ScratchCardState.values().firstOrNull { it.name == this } ?: ScratchCardState.NEW
}