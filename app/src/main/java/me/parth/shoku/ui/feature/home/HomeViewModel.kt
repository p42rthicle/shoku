package me.parth.shoku.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.parth.shoku.domain.model.LoggedEntry
import me.parth.shoku.domain.repository.FoodRepository
import me.parth.shoku.ui.feature.addfood.MviViewModel // Reusing MviViewModel interface
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel(), MviViewModel<HomeContract.UiState, HomeContract.Intent, HomeContract.Effect> {

    private val _uiState = MutableStateFlow(HomeContract.UiState())
    override val uiState: StateFlow<HomeContract.UiState> = _uiState.asStateFlow()

    private val _effect = Channel<HomeContract.Effect>(Channel.BUFFERED)
    override val effect: Flow<HomeContract.Effect> = _effect.receiveAsFlow()

    init {
        // Load data for the initial date when the ViewModel is created
        loadDataForDate(uiState.value.selectedDate)
    }

    override fun onIntent(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.ChangeDate -> {
                // Update state immediately and trigger data loading
                _uiState.update { it.copy(selectedDate = intent.date, isLoading = true, error = null) }
                loadDataForDate(intent.date)
            }
            is HomeContract.Intent.LoadDataForSelectedDate -> {
                // Explicit reload for the current date
                loadDataForDate(uiState.value.selectedDate)
            }
            is HomeContract.Intent.OpenTargetSettings -> {
                sendEffect(HomeContract.Effect.NavigateToTargetSettings)
            }
        }
    }

    private fun loadDataForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) } // Show loading

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

    private fun sendEffect(effectToSend: HomeContract.Effect) {
        viewModelScope.launch {
            _effect.send(effectToSend)
        }
    }
} 