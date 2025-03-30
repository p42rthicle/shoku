package me.parth.shoku.ui.feature.addfood

import me.parth.shoku.domain.model.Meal
import me.parth.shoku.domain.model.FoodItem

/**
 * Defines the contract between the UI and the ViewModel for the Add Food screen,
 * following the MVI pattern.
 */
interface AddFoodContract {

    /**
     * Represents the state of the Add Food screen UI.
     *
     * @param foodName Current value of the food name input.
     * @param quantity Current value of the quantity input.
     * @param unit Current selected unit.
     * @param calories Current value of the calories input.
     * @param protein Current value of the protein input.
     * @param selectedMeal Current selected meal.
     * @param notes Current value of the notes input.
     * @param availableUnits List of units for the dropdown.
     * @param availableMeals List of meals for selection.
     * @param isLoading Indicates if a save operation is in progress.
     * @param suggestions List of food items for suggestions.
     * @param baseCaloriesPerUnit Calories of the suggested item per its default unit
     * @param baseProteinPerUnit Protein of the suggested item per its default unit
     * @param suggestionSelected Flag to indicate if current values came from a suggestion
     */
    data class UiState(
        val foodName: String = "",
        val quantity: String = "", // Use String for input flexibility
        val unit: String = "g", // Default unit
        val calories: String = "", // Use String for input flexibility
        val protein: String = "", // Use String for input flexibility
        val selectedMeal: Meal = Meal.BREAKFAST, // Default meal
        val notes: String = "",
        val availableUnits: List<String> = listOf("g", "ml", "pc", "cup", "slice", "katori"),
        val availableMeals: List<Meal> = Meal.entries.toList(),
        val isLoading: Boolean = false,
        val suggestions: List<FoodItem> = emptyList(),
        val baseCaloriesPerUnit: Double? = null, // Calories of the suggested item per its default unit
        val baseProteinPerUnit: Double? = null, // Protein of the suggested item per its default unit
        val suggestionSelected: Boolean = false // Flag to indicate if current values came from a suggestion
    )

    /**
     * Represents user actions or events initiated from the UI.
     */
    sealed interface Intent {
        data class UpdateFoodName(val name: String) : Intent
        data class UpdateQuantity(val quantity: String) : Intent
        data class UpdateUnit(val unit: String) : Intent
        data class UpdateCalories(val calories: String) : Intent
        data class UpdateProtein(val protein: String) : Intent
        data class UpdateSelectedMeal(val meal: Meal) : Intent
        data class UpdateNotes(val notes: String) : Intent
        object SaveEntry : Intent
        data class SelectSuggestion(val foodItem: FoodItem) : Intent
    }

    /**
     * Represents side effects that the ViewModel triggers, usually for one-time events
     * like navigation or showing a toast/snackbar.
     */
    sealed interface Effect {
        object EntrySavedSuccessfully : Effect
        data class ShowError(val message: String) : Effect
    }
} 