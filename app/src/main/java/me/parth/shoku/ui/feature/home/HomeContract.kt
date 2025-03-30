package me.parth.shoku.ui.feature.home

import me.parth.shoku.domain.model.LoggedEntry
import java.time.LocalDate

/**
 * Defines the contract between the UI (HomeScreen) and the ViewModel (HomeViewModel)
 * following the MVI pattern.
 */
interface HomeContract {

    /**
     * Represents the state of the HomeScreen UI.
     *
     * @param selectedDate The currently displayed date.
     * @param dailyEntries List of food entries for the selected date.
     * @param totalCalories Sum of calories for the selected date.
     * @param totalProtein Sum of protein for the selected date.
     * @param calorieTarget User's daily calorie goal.
     * @param proteinTarget User's daily protein goal.
     * @param isLoading Indicates if data is currently being loaded.
     * @param error A message describing an error, if one occurred.
     */
    data class UiState(
        val selectedDate: LocalDate = LocalDate.now(), // Default to today
        val dailyEntries: List<LoggedEntry> = emptyList(),
        val totalCalories: Double = 0.0,
        val totalProtein: Double = 0.0,
        val calorieTarget: Double = 2000.0, // Default target
        val proteinTarget: Double = 100.0, // Default target
        val isLoading: Boolean = false,
        val error: String? = null
    )

    /**
     * Represents user actions or events originating from the UI.
     */
    sealed interface Intent {
        data class ChangeDate(val date: LocalDate) : Intent
        data object LoadDataForSelectedDate : Intent // Explicit intent to load/reload
        data object OpenTargetSettings : Intent
        // Add other intents like DeleteEntry if needed later
    }

    /**
     * Represents side effects triggered by the ViewModel, usually for navigation or
     * showing temporary messages (like Snackbars).
     */
    sealed interface Effect {
        data object NavigateToTargetSettings : Effect
        data class ShowError(val message: String) : Effect
    }
} 