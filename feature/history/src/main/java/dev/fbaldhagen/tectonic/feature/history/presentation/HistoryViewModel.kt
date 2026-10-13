package dev.fbaldhagen.tectonic.feature.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.service.DomainClock
import dev.fbaldhagen.tectonic.domain.usecase.GetGameHistoryUseCase
import dev.fbaldhagen.tectonic.ui.text.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getGameHistoryUseCase: GetGameHistoryUseCase,
    private val clock: DomainClock,
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
                    it.copy(
                        isLoading = false,
                        sections = groupHistory(
                            games = result.data,
                            now = clock.now(),
                            zone = ZoneId.systemDefault(),
                            weekFields = WeekFields.of(Locale.getDefault()),
                        ),
                    )
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
