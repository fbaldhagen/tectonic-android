package dev.fbaldhagen.tectonic.feature.home.presentation

import dev.fbaldhagen.tectonic.domain.model.Game

internal fun Game.toHomeUiModel(): HomeInProgressGameUiModel =
    HomeInProgressGameUiModel(
        gameId = id.value,
        difficulty = puzzle.difficulty,
        gridSize = puzzle.gridSize,
    )
