package dev.fbaldhagen.tectonic.feature.statistics.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.usecase.GetStatisticsUseCase
import dev.fbaldhagen.tectonic.ui.text.toUiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getStatisticsUseCase: GetStatisticsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun onAction(action: StatisticsAction) {
        when (action) {
            is StatisticsAction.SelectDifficulty -> {
                if (action.difficulty == _uiState.value.selectedDifficulty) return
                _uiState.update { it.copy(selectedDifficulty = action.difficulty) }
                load()
            }

            StatisticsAction.Retry -> load()
        }
    }

    private fun load() {
        val difficulty = _uiState.value.selectedDifficulty

        // A newer filter choice supersedes any load still in flight.
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            // Keep showing the previous numbers while a new filter loads, to avoid flicker.
            _uiState.update { it.copy(isLoading = it.statistics == null, error = null) }

            when (val result = getStatisticsUseCase(difficulty)) {
                is TecResult.Success -> _uiState.update {
                    it.copy(isLoading = false, statistics = result.data)
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
