package dev.fbaldhagen.tectonic.domain.repository

import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.domain.model.Game
import dev.fbaldhagen.tectonic.domain.model.GameId

interface GameProgressRepository {

    suspend fun getGame(
        gameId: GameId
    ): TecResult<Game>

    suspend fun getMostRecentGame(): TecResult<Game?>

    suspend fun saveGame(
        game: Game
    ): TecResult<Unit>

    suspend fun deleteGame(
        gameId: GameId
    ): TecResult<Unit>
}