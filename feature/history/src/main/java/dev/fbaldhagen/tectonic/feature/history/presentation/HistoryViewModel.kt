package dev.fbaldhagen.tectonic.feature.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.usecase.GetGameHistoryUseCase
import dev.fbaldhagen.tectonic.ui.text.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getGameHistoryUseCase: GetGameHistoryUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun onAction(action: HistoryAction) {
        when (action) {
            HistoryAction.Retry -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getGameHistoryUseCase()) {
                is TecResult.Success -> _uiState.update {
                    it.copy(isLoading = false, games = result.data)
                }

                is TecResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.error.toUiText())
                    }
                }
            }
        }
    }
}
