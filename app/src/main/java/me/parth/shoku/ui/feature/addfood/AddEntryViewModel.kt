package me.parth.shoku.ui.feature.addfood

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.parth.shoku.domain.model.FoodItem
import me.parth.shoku.domain.model.LoggedEntry
import me.parth.shoku.domain.repository.FoodRepository
import me.parth.shoku.ui.navigation.Screen
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kotlin.math.roundToInt

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddEntryViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), MviViewModel<AddFoodContract.UiState, AddFoodContract.Intent, AddFoodContract.Effect> {

    private val entryDate: LocalDate = savedStateHandle.get<String>(Screen.AddFood.dateArg)?.let {
        try { LocalDate.parse(it) } catch (e: DateTimeParseException) { null }
    } ?: LocalDate.now()

    private val _uiState = MutableStateFlow(AddFoodContract.UiState())
    override val uiState = _uiState.asStateFlow()

    private val _effect = Channel<AddFoodContract.Effect>(Channel.BUFFERED)
    override val effect = _effect.receiveAsFlow()

    init {
        observeFoodNameChanges()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeFoodNameChanges() {
        viewModelScope.launch {
            _uiState.map { it.foodName }
                .distinctUntilChanged() // Only react to actual changes
                .debounce(300L) // Wait for 300ms pause in typing
                .flatMapLatest { name -> // Cancel previous flow if name changes quickly
                    if (name.length < 2) { // Don't search for very short strings
                        flowOf(emptyList()) // Return empty list immediately
                    } else {
                        foodRepository.getFoodSuggestions(name)
                            .catch { e -> // Handle errors during suggestion fetching
                                // Log error e
                                emit(emptyList()) // Emit empty list on error
                            }
                    }
                }
                .collect { suggestions ->
                    _uiState.update { it.copy(suggestions = suggestions) }
                }
        }
    }

    override fun onIntent(intent: AddFoodContract.Intent) {
        when (intent) {
            is AddFoodContract.Intent.UpdateFoodName -> {
                // Reset suggestion flag if user types manually
                _uiState.update { it.copy(foodName = intent.name, suggestionSelected = false) }
            }
            is AddFoodContract.Intent.SelectSuggestion -> handleSuggestionSelection(intent.foodItem)
            is AddFoodContract.Intent.UpdateQuantity -> {
                // Recalculate if a suggestion was selected and base values are available
                val currentState = uiState.value
                val newQuantity = intent.quantity.toDoubleOrNull()
                if (currentState.suggestionSelected && newQuantity != null && newQuantity > 0 && currentState.baseCaloriesPerUnit != null && currentState.baseProteinPerUnit != null) {
                    val newCalories = (currentState.baseCaloriesPerUnit * newQuantity).roundToInt()
                    val newProtein = (currentState.baseProteinPerUnit * newQuantity).roundToInt()
                    _uiState.update {
                        it.copy(
                            quantity = intent.quantity,
                            calories = newCalories.toString(),
                            protein = newProtein.toString()
                        )
                    }
                } else {
                    // Otherwise, just update quantity and clear flag
                    _uiState.update { it.copy(quantity = intent.quantity, suggestionSelected = false) }
                }
            }
            is AddFoodContract.Intent.UpdateUnit -> _uiState.update { it.copy(unit = intent.unit, suggestionSelected = false) } // Clear flag
            is AddFoodContract.Intent.UpdateCalories -> _uiState.update { it.copy(calories = intent.calories, suggestionSelected = false) } // Clear flag
            is AddFoodContract.Intent.UpdateProtein -> _uiState.update { it.copy(protein = intent.protein, suggestionSelected = false) } // Clear flag
            is AddFoodContract.Intent.UpdateSelectedMeal -> _uiState.update { it.copy(selectedMeal = intent.meal) }
            is AddFoodContract.Intent.UpdateNotes -> _uiState.update { it.copy(notes = intent.notes) }
            AddFoodContract.Intent.SaveEntry -> saveEntry()
        }
    }

    private fun handleSuggestionSelection(foodItem: FoodItem) {
        _uiState.update {
            it.copy(
                foodName = foodItem.name,
                quantity = "1",
                unit = foodItem.defaultUnit ?: it.unit,
                calories = foodItem.calories.roundToInt().toString(), // Use base value initially
                protein = foodItem.protein.roundToInt().toString(), // Use base value initially
                suggestions = emptyList(),
                // Store base values and set flag
                baseCaloriesPerUnit = foodItem.calories,
                baseProteinPerUnit = foodItem.protein,
                suggestionSelected = true
            )
        }
    }

    private fun saveEntry() {
        val currentState = _uiState.value
        val quantity = currentState.quantity.toDoubleOrNull()
        val calories = currentState.calories.toDoubleOrNull()
        val protein = currentState.protein.toDoubleOrNull()

        // Basic Validation
        if (currentState.foodName.isBlank() || quantity == null || calories == null || protein == null || quantity <= 0) {
            sendEffect(AddFoodContract.Effect.ShowError("Please fill in all required fields correctly (Name, Quantity > 0, Calories, Protein)."))
            return
        }

        val entry = LoggedEntry(
            foodName = currentState.foodName.trim(),
            quantity = quantity,
            unit = currentState.unit,
            calories = calories,
            protein = protein,
            meal = currentState.selectedMeal,
            notes = currentState.notes.takeIf { it.isNotBlank() },
            date = entryDate
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                foodRepository.addLoggedEntry(entry)
                sendEffect(AddFoodContract.Effect.EntrySavedSuccessfully)
                 _uiState.value = AddFoodContract.UiState() // Reset state
            } catch (e: Exception) {
                sendEffect(AddFoodContract.Effect.ShowError("Failed to save entry: ${e.localizedMessage}"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun sendEffect(effectToSend: AddFoodContract.Effect) {
        viewModelScope.launch {
            _effect.send(effectToSend)
        }
    }
}

// Extend ViewModel to handle intents easily from UI
interface MviViewModel<S, I, E> {
    val uiState: StateFlow<S>
    val effect: Flow<E>
    fun onIntent(intent: I)
} 