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
import android.util.Log

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddEntryViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), MviViewModel<AddFoodContract.UiState, AddFoodContract.Intent, AddFoodContract.Effect> {

    // Read entryId argument, -1 indicates new entry
    private val entryId: Long = savedStateHandle.get<Long>(Screen.AddFood.entryIdArg) ?: -1L
    private val isEditMode = entryId != -1L

    // Determine entry date from arg or default to now (only for new entries)
    private val entryDate: LocalDate = if (isEditMode) {
        // In edit mode, date comes from the fetched entry (set during load)
        LocalDate.now() // This becomes just a temporary default before load
    } else {
        // For new entries, get from argument or default to now
        savedStateHandle.get<String>(Screen.AddFood.dateArg)?.let {
            try { LocalDate.parse(it) } catch (e: DateTimeParseException) { null }
        } ?: LocalDate.now()
    }

    private val _uiState = MutableStateFlow(AddFoodContract.UiState(date = entryDate))
    override val uiState = _uiState.asStateFlow()

    private val _effect = Channel<AddFoodContract.Effect>(Channel.BUFFERED)
    override val effect = _effect.receiveAsFlow()

    init {
        if (isEditMode) {
            loadEntryForEdit()
        } else {
            // Initialize for new entry (suggestion observation)
            observeFoodNameChanges()
        }
    }

    private fun loadEntryForEdit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Show loading while fetching
            val entry = foodRepository.getEntryById(entryId)
            if (entry != null) {
                _uiState.update {
                    it.copy(
                        foodName = entry.foodName,
                        quantity = entry.quantity.toString(),
                        unit = entry.unit,
                        calories = entry.calories.roundToInt().toString(),
                        protein = entry.protein.roundToInt().toString(),
                        selectedMeal = entry.meal,
                        notes = entry.notes ?: "",
                        date = entry.date,
                        isLoading = false
                    )
                }
                // Start observing suggestions AFTER pre-filling, if desired
                observeFoodNameChanges()
            } else {
                _uiState.update { it.copy(isLoading = false) }
                sendEffect(AddFoodContract.Effect.ShowError("Failed to load entry for editing"))
                // Optionally navigate back or show permanent error
            }
        }
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
                 // Clear base values and base unit
                 _uiState.update { it.copy(foodName = intent.name, baseCaloriesPerUnit = null, baseProteinPerUnit = null, baseUnit = null) }
            }
            is AddFoodContract.Intent.SelectSuggestion -> handleSuggestionSelection(intent.foodItem)
            is AddFoodContract.Intent.UpdateQuantity -> {
                 val currentState = uiState.value
                 val newQuantity = intent.quantity.toDoubleOrNull()
                 // Recalculate if base values exist AND current unit matches base unit
                 if (newQuantity != null && newQuantity > 0 && currentState.baseCaloriesPerUnit != null && currentState.baseProteinPerUnit != null && currentState.unit == currentState.baseUnit) {
                     Log.d("AddEntryVM", "Recalculating totals based on base values...")
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
                    Log.d("AddEntryVM", "Updating quantity only (base values null, unit mismatch, or invalid qty).")
                    _uiState.update { it.copy(quantity = intent.quantity) }
                 }
            }
            is AddFoodContract.Intent.UpdateUnit -> {
                val currentState = uiState.value
                val newUnit = intent.unit
                // Check if we have base values stored from a previous suggestion
                if (currentState.baseUnit != null) {
                    // If new unit matches the original base unit, keep base values; otherwise clear them
                     _uiState.update {
                        it.copy(
                            unit = newUnit,
                            baseCaloriesPerUnit = if (newUnit == currentState.baseUnit) it.baseCaloriesPerUnit else null,
                            baseProteinPerUnit = if (newUnit == currentState.baseUnit) it.baseProteinPerUnit else null
                        )
                    }
                } else {
                    // No base unit stored, just update the unit
                    _uiState.update { it.copy(unit = newUnit) }
                }
            }
            is AddFoodContract.Intent.UpdateCalories -> _uiState.update { it.copy(calories = intent.calories, baseCaloriesPerUnit = null, baseProteinPerUnit = null, baseUnit = null) }
            is AddFoodContract.Intent.UpdateProtein -> _uiState.update { it.copy(protein = intent.protein, baseCaloriesPerUnit = null, baseProteinPerUnit = null, baseUnit = null) }
            is AddFoodContract.Intent.UpdateSelectedMeal -> _uiState.update { it.copy(selectedMeal = intent.meal) }
            is AddFoodContract.Intent.UpdateNotes -> _uiState.update { it.copy(notes = intent.notes) }
            AddFoodContract.Intent.SaveEntry -> saveEntry()
        }
    }

    private fun handleSuggestionSelection(foodItem: FoodItem) {
        val suggestedUnit = foodItem.defaultUnit // Get the unit from the suggestion
        _uiState.update {
            it.copy(
                foodName = foodItem.name,
                quantity = "1",
                unit = suggestedUnit ?: it.unit, // Use suggested unit
                calories = foodItem.calories.roundToInt().toString(),
                protein = foodItem.protein.roundToInt().toString(),
                suggestions = emptyList(),
                baseCaloriesPerUnit = foodItem.calories,
                baseProteinPerUnit = foodItem.protein,
                baseUnit = suggestedUnit // Store the base unit
            )
        }
    }

    private fun saveEntry() {
        val currentState = uiState.value
        val quantity = currentState.quantity.toDoubleOrNull()
        val calories = currentState.calories.toDoubleOrNull()
        val protein = currentState.protein.toDoubleOrNull()

        // Basic Validation
        if (currentState.foodName.isBlank() || quantity == null || calories == null || protein == null || quantity <= 0) {
            sendEffect(AddFoodContract.Effect.ShowError("Please fill in all required fields correctly (Name, Quantity > 0, Calories, Protein)."))
            return
        }

        val entry = LoggedEntry(
            id = if (isEditMode) entryId else 0L,
            foodName = currentState.foodName.trim(),
            quantity = quantity,
            unit = currentState.unit,
            calories = calories,
            protein = protein,
            meal = currentState.selectedMeal,
            notes = currentState.notes.takeIf { it.isNotBlank() },
            date = currentState.date
        )

        viewModelScope.launch {
             _uiState.update { it.copy(isLoading = true) }
             try {
                 if (isEditMode) {
                     foodRepository.updateLoggedEntry(entry)
                 } else {
                     foodRepository.addLoggedEntry(entry)
                 }
                 sendEffect(AddFoodContract.Effect.EntrySavedSuccessfully)
                 // Consider not resetting state in edit mode, just navigate back?
                 if (!isEditMode) {
                    _uiState.value = AddFoodContract.UiState() // Reset state only for new entries
                 }
             } catch (e: Exception) {
                 sendEffect(AddFoodContract.Effect.ShowError("Failed to ${if (isEditMode) "update" else "save"} entry: ${e.localizedMessage}"))
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