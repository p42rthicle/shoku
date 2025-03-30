package me.parth.shoku.ui.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.parth.shoku.domain.model.LoggedEntry
import me.parth.shoku.domain.repository.FoodRepository
import me.parth.shoku.ui.feature.addfood.MviViewModel
import me.parth.shoku.ui.navigation.Screen
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kotlin.math.roundToInt // Import for rounding

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), MviViewModel<HomeContract.UiState, HomeContract.Intent, HomeContract.Effect> {

    private val initialDate: LocalDate = savedStateHandle.get<String>(Screen.Home.dateArg)?.let {
        try { LocalDate.parse(it) } catch (e: DateTimeParseException) { null }
    } ?: LocalDate.now()

    private val _uiState = MutableStateFlow(HomeContract.UiState(selectedDate = initialDate))
    override val uiState: StateFlow<HomeContract.UiState> = _uiState.asStateFlow()

    private val _effect = Channel<HomeContract.Effect>(Channel.BUFFERED)
    override val effect: Flow<HomeContract.Effect> = _effect.receiveAsFlow()

    init {
        // Load data for the initial date when the ViewModel is created
        loadDataForDate(initialDate)
        // Initialize input fields with current targets when VM starts
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    calorieTargetInput = foodRepository.getCalorieTarget().roundToInt().toString(),
                    proteinTargetInput = foodRepository.getProteinTarget().roundToInt().toString()
                )
            }
        }
    }

    override fun onIntent(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.ChangeDate -> {
                _uiState.update { it.copy(selectedDate = intent.date, isLoading = true, error = null) }
                loadDataForDate(intent.date)
            }
            is HomeContract.Intent.LoadDataForSelectedDate -> {
                loadDataForDate(uiState.value.selectedDate)
            }
            is HomeContract.Intent.OpenTargetSettings -> {
                // Load current targets into input fields when opening settings
                viewModelScope.launch {
                     _uiState.update {
                        it.copy(
                            calorieTargetInput = it.calorieTarget.roundToInt().toString(),
                            proteinTargetInput = it.proteinTarget.roundToInt().toString()
                        )
                    }
                    sendEffect(HomeContract.Effect.NavigateToTargetSettings)
                }
            }
            // Handle new intents
            is HomeContract.Intent.UpdateCalorieTargetInput -> {
                _uiState.update { it.copy(calorieTargetInput = intent.value) }
            }
            is HomeContract.Intent.UpdateProteinTargetInput -> {
                _uiState.update { it.copy(proteinTargetInput = intent.value) }
            }
            is HomeContract.Intent.SaveTargets -> {
                saveTargets()
            }
            is HomeContract.Intent.RefreshTargets -> {
                refreshTargets()
            }
            is HomeContract.Intent.DeleteEntry -> {
                deleteEntry(intent.entry)
            }
        }
    }

    private fun loadDataForDate(date: LocalDate) {
        viewModelScope.launch {
            // Only show loading indicator if data isn't already loaded for this date
            // or if triggered by an explicit reload intent (if we add one)
            val showLoading = uiState.value.dailyEntries.isEmpty() || uiState.value.selectedDate != date
            if (showLoading) {
                 _uiState.update { it.copy(isLoading = true, error = null) }
            } else {
                 // If not showing full loading, still clear previous error
                 _uiState.update { it.copy(error = null) }
            }

            try {
                // Fetch targets first (or potentially combine with entry fetching)
                val calorieTarget = foodRepository.getCalorieTarget()
                val proteinTarget = foodRepository.getProteinTarget()

                // Fetch entries for the date and calculate totals
                foodRepository.getEntriesForDate(date)
                    .catch { e ->
                        // Handle error during entry fetching
                        _uiState.update {
                            it.copy(
                                dailyEntries = emptyList(),
                                totalCalories = 0.0,
                                totalProtein = 0.0,
                                calorieTarget = calorieTarget, // Still update targets if fetched
                                proteinTarget = proteinTarget,
                                isLoading = false,
                                error = "Failed to load entries: ${e.localizedMessage}"
                            )
                        }
                        sendEffect(HomeContract.Effect.ShowError("Failed to load entries"))
                    }
                    .collect { entries ->
                        val totalCalories = entries.sumOf { it.calories }
                        val totalProtein = entries.sumOf { it.protein }

                        _uiState.update {
                            it.copy(
                                dailyEntries = entries,
                                totalCalories = totalCalories,
                                totalProtein = totalProtein,
                                calorieTarget = calorieTarget,
                                proteinTarget = proteinTarget,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                // Handle error during target fetching (or other general errors)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load data: ${e.localizedMessage}"
                    )
                }
                sendEffect(HomeContract.Effect.ShowError("Failed to load data"))
            }
        }
    }

    private fun saveTargets() {
        val currentState = uiState.value
        val calorieTargetDouble = currentState.calorieTargetInput.toDoubleOrNull()
        val proteinTargetDouble = currentState.proteinTargetInput.toDoubleOrNull()

        if (calorieTargetDouble == null || calorieTargetDouble <= 0 || proteinTargetDouble == null || proteinTargetDouble <= 0) {
            sendEffect(HomeContract.Effect.ShowError("Please enter valid positive numbers for targets."))
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingTarget = true) }
            try {
                foodRepository.saveCalorieTarget(calorieTargetDouble)
                foodRepository.saveProteinTarget(proteinTargetDouble)
                // Update the main state targets as well after saving
                _uiState.update {
                     it.copy(
                         calorieTarget = calorieTargetDouble,
                         proteinTarget = proteinTargetDouble,
                         isSavingTarget = false
                     )
                 }
                sendEffect(HomeContract.Effect.TargetsSavedSuccessfully)
            } catch (e: Exception) {
                _uiState.update { it.copy(isSavingTarget = false) }
                sendEffect(HomeContract.Effect.ShowError("Failed to save targets: ${e.localizedMessage}"))
            }
        }
    }

    private fun refreshTargets() {
        viewModelScope.launch {
            // Only fetch targets and update those specific fields
            try {
                val calorieTarget = foodRepository.getCalorieTarget()
                val proteinTarget = foodRepository.getProteinTarget()
                _uiState.update {
                    it.copy(
                        calorieTarget = calorieTarget,
                        proteinTarget = proteinTarget
                        // Potentially re-initialize inputs if needed, but maybe not desirable here
                        // calorieTargetInput = calorieTarget.roundToInt().toString(),
                        // proteinTargetInput = proteinTarget.roundToInt().toString()
                    )
                }
            } catch (e: Exception) {
                 sendEffect(HomeContract.Effect.ShowError("Failed to refresh targets: ${e.localizedMessage}"))
            }
        }
    }

    private fun deleteEntry(entry: LoggedEntry) {
        viewModelScope.launch {
            try {
                foodRepository.deleteLoggedEntry(entry)
                // No need to manually remove from list, as the Flow from
                // getEntriesForDate should automatically update after deletion.
                // Optionally send a success feedback effect:
                // sendEffect(HomeContract.Effect.ShowMessage("Entry deleted"))
            } catch (e: Exception) {
                sendEffect(HomeContract.Effect.ShowError("Failed to delete entry: ${e.localizedMessage}"))
            }
        }
    }

    private fun sendEffect(effectToSend: HomeContract.Effect) {
        viewModelScope.launch {
            _effect.send(effectToSend)
        }
    }
} 