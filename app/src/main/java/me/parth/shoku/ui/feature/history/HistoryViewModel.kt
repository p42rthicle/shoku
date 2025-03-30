package me.parth.shoku.ui.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.parth.shoku.domain.repository.FoodRepository
import me.parth.shoku.ui.feature.addfood.MviViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel(),
    MviViewModel<HistoryContract.UiState, HistoryContract.Intent, HistoryContract.Effect> {

    private val _uiState = MutableStateFlow(HistoryContract.UiState())
    override val uiState: StateFlow<HistoryContract.UiState> = _uiState.asStateFlow()

    private val _effect = Channel<HistoryContract.Effect>(Channel.BUFFERED)
    override val effect: Flow<HistoryContract.Effect> = _effect.receiveAsFlow()

    init {
        loadHistory()
    }

    override fun onIntent(intent: HistoryContract.Intent) {
        when (intent) {
            is HistoryContract.Intent.SelectDay -> {
                sendEffect(HistoryContract.Effect.NavigateToDayDetail(intent.date))
            }
            is HistoryContract.Intent.RetryLoad -> {
                loadHistory()
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            foodRepository.getDailySummaries()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load history: ${e.localizedMessage}"
                        )
                    }
                     sendEffect(HistoryContract.Effect.ShowError("Failed to load history"))
                }
                .collect { summaries ->
                    _uiState.update {
                        it.copy(
                            dailySummaries = summaries,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

     private fun sendEffect(effectToSend: HistoryContract.Effect) {
        viewModelScope.launch {
            _effect.send(effectToSend)
        }
    }
} 