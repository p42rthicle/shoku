package me.parth.shoku.ui.feature.history

import me.parth.shoku.domain.model.DailySummary
import java.time.LocalDate

/**
 * Defines the contract between the UI (HistoryScreen) and the ViewModel (HistoryViewModel)
 * following the MVI pattern.
 */
interface HistoryContract {

    /**
     * Represents the state of the HistoryScreen UI.
     */
    data class UiState(
        val dailySummaries: List<DailySummary> = emptyList(),
        val isLoading: Boolean = true, // Start as loading
        val error: String? = null
    )

    /**
     * Represents user actions or events originating from the UI.
     */
    sealed interface Intent {
        data class SelectDay(val date: LocalDate) : Intent
        data object RetryLoad : Intent // Optional: If error occurs
    }

    /**
     * Represents side effects triggered by the ViewModel.
     */
    sealed interface Effect {
        data class NavigateToDayDetail(val date: LocalDate) : Effect
        data class ShowError(val message: String) : Effect // Generic error for Snackbar
    }
} 