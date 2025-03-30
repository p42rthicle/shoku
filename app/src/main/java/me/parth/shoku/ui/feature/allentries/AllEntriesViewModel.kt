package me.parth.shoku.ui.feature.allentries

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
class AllEntriesViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel(),
    MviViewModel<AllEntriesContract.UiState, AllEntriesContract.Intent, AllEntriesContract.Effect> {

    private val _uiState = MutableStateFlow(AllEntriesContract.UiState())
    override val uiState: StateFlow<AllEntriesContract.UiState> = _uiState.asStateFlow()

    private val _effect = Channel<AllEntriesContract.Effect>(Channel.BUFFERED)
    override val effect: Flow<AllEntriesContract.Effect> = _effect.receiveAsFlow()

    init {
        loadAllEntries()
    }

    override fun onIntent(intent: AllEntriesContract.Intent) {
        when(intent) {
            AllEntriesContract.Intent.RetryLoad -> loadAllEntries()
        }
    }

    private fun loadAllEntries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            foodRepository.getAllLoggedEntries()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load all entries: ${e.localizedMessage}"
                        )
                    }
                    sendEffect(AllEntriesContract.Effect.ShowError("Failed to load entries"))
                }
                .collect { entries ->
                     _uiState.update {
                        it.copy(
                            allEntries = entries,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun sendEffect(effectToSend: AllEntriesContract.Effect) {
        viewModelScope.launch {
            _effect.send(effectToSend)
        }
    }
} 