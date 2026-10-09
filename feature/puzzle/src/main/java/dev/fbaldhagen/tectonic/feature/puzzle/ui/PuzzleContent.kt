package dev.fbaldhagen.tectonic.feature.puzzle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.fbaldhagen.tectonic.feature.puzzle.R
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleAction
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleUiMessage
import dev.fbaldhagen.tectonic.feature.puzzle.presentation.PuzzleUiState
import dev.fbaldhagen.tectonic.ui.board.TecBoard

@Composable
fun PuzzleContent(
    state: PuzzleUiState,
    onAction: (PuzzleAction) -> Unit,
    onNavigateHome: () -> Unit,
) {
    when {
        state.isLoading -> PuzzleLoading()
        state.grid != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                state.difficulty?.let { difficulty ->
                    Text(
                        text = stringResource(
                            when (difficulty) {
                                dev.fbaldhagen.tectonic.domain.model.Difficulty.EASY -> R.string.puzzle_difficulty_easy
                                dev.fbaldhagen.tectonic.domain.model.Difficulty.MEDIUM -> R.string.puzzle_difficulty_medium
                                dev.fbaldhagen.tectonic.domain.model.Difficulty.HARD -> R.string.puzzle_difficulty_hard
                            },
                        ),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                if (state.isCompleted) {
                    Text(
                        text = stringResource(R.string.puzzle_solution_solved),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                TecBoard(
                    board = state.grid,
                    selectedCell = state.selectedCell,
                    incorrectCells = state.solutionCheck?.incorrectCells.orEmpty(),
                    modifier = Modifier.weight(1f),
                    onCellClick = { onAction(PuzzleAction.SelectCell(it)) },
                )

                state.selectedCell?.takeIf { !state.isCompleted }?.let { it ->
                    PuzzleNumberPad(
                        maxValue = state.grid.cells
                            .firstOrNull { cell -> cell.cellId == it }
                            ?.regionId
                            ?.let { regionId ->
                                state.grid.cells.count { cell -> cell.regionId == regionId }
                            }
                            ?: 1,
                        onValueSelected = { onAction(PuzzleAction.EnterValue(it)) },
                        onClear = { onAction(PuzzleAction.ClearCell) },
                    )
                }

                state.moveValidation?.let { validation ->
                    if (!validation.isValid) {
                        Text(
                            text = stringResource(R.string.puzzle_invalid_move),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                state.message?.let { message ->
                    Text(
                        text = when (message) {
                            PuzzleUiMessage.NO_ACTIVE_GAME -> stringResource(R.string.puzzle_no_active_game)
                            PuzzleUiMessage.GENERIC_ERROR -> stringResource(R.string.puzzle_generic_error)
                        },
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                state.solutionCheck?.let { result ->
                    val text = when (result.status) {
                        dev.fbaldhagen.tectonic.domain.model.SolutionStatus.SOLVED ->
                            stringResource(R.string.puzzle_solution_solved)
                        dev.fbaldhagen.tectonic.domain.model.SolutionStatus.INCOMPLETE ->
                            stringResource(R.string.puzzle_solution_incomplete)
                        dev.fbaldhagen.tectonic.domain.model.SolutionStatus.INCORRECT ->
                            stringResource(R.string.puzzle_solution_incorrect)
                    }
                    Text(text = text)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = { onAction(PuzzleAction.Undo) },
                        enabled = !state.isCompleted,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.puzzle_undo))
                    }
                    OutlinedButton(
                        onClick = { onAction(PuzzleAction.CheckSolution) },
                        enabled = !state.isCompleted,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.puzzle_check))
                    }
                    OutlinedButton(
                        onClick = onNavigateHome,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.puzzle_home))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DifficultyButton(Modifier.weight(1f), R.string.puzzle_new_easy) {
                        onAction(PuzzleAction.StartNewGame(dev.fbaldhagen.tectonic.domain.model.Difficulty.EASY))
                    }
                    DifficultyButton(Modifier.weight(1f), R.string.puzzle_new_medium) {
                        onAction(PuzzleAction.StartNewGame(dev.fbaldhagen.tectonic.domain.model.Difficulty.MEDIUM))
                    }
                    DifficultyButton(Modifier.weight(1f), R.string.puzzle_new_hard) {
                        onAction(PuzzleAction.StartNewGame(dev.fbaldhagen.tectonic.domain.model.Difficulty.HARD))
                    }
                }
            }
        }
        state.message != null -> {
            PuzzleMessage(
                message = state.message,
                onDismiss = { onAction(PuzzleAction.DismissMessage) },
                onNewGame = { onAction(PuzzleAction.StartNewGame(dev.fbaldhagen.tectonic.domain.model.Difficulty.EASY)) },
                onNavigateHome = onNavigateHome,
            )
        }
    }
}

@Composable
private fun DifficultyButton(
    modifier: Modifier,
    labelResId: Int,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(stringResource(labelResId))
    }
}

@Composable
private fun PuzzleMessage(
    message: PuzzleUiMessage,
    onDismiss: () -> Unit,
    onNewGame: () -> Unit,
    onNavigateHome: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val text = when (message) {
            PuzzleUiMessage.NO_ACTIVE_GAME -> stringResource(R.string.puzzle_no_active_game)
            PuzzleUiMessage.GENERIC_ERROR -> stringResource(R.string.puzzle_generic_error)
        }
        Text(text = text, style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onNewGame) {
                Text(stringResource(R.string.puzzle_start_easy))
            }
            OutlinedButton(onClick = onNavigateHome) {
                Text(stringResource(R.string.puzzle_home))
            }
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.puzzle_dismiss))
            }
        }
    }
}