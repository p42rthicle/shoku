package me.parth.shoku.ui.feature.allentries

import me.parth.shoku.domain.model.LoggedEntry

/**
 * Contract for the All Entries (Raw Log) screen.
 */
interface AllEntriesContract {

    data class UiState(
        val allEntries: List<LoggedEntry> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null
    )

    // No intents needed for basic view-only screen initially
    sealed interface Intent {
        data object RetryLoad : Intent // Optional
    }

    // No effects needed for basic view-only screen initially
    sealed interface Effect {
        data class ShowError(val message: String): Effect
    }
} 