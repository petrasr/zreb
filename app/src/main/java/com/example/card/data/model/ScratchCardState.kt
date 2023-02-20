package com.example.card.data.model

import androidx.annotation.StringRes
import com.example.card.R

enum class ScratchCardState(@StringRes val label: Int) {
    NEW(R.string.scratch_card_state_new),
    SCRATCHED(R.string.scratch_card_state_scratched),
    ACTIVATED(R.string.scratch_card_state_activated)
}
