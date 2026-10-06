package dev.fbaldhagen.tectonic.data.repository

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.common.TecResult
import dev.fbaldhagen.tectonic.common.suspendRunCatching
import dev.fbaldhagen.tectonic.common.toAppError
import dev.fbaldhagen.tectonic.common.toException
import dev.fbaldhagen.tectonic.data.local.dao.PuzzleDao
import dev.fbaldhagen.tectonic.data.local.mapper.toDomain
import dev.fbaldhagen.tectonic.data.local.mapper.toEntity
import dev.fbaldhagen.tectonic.domain.model.Puzzle
import dev.fbaldhagen.tectonic.domain.model.PuzzleId
import dev.fbaldhagen.tectonic.domain.repository.PuzzleRepository
import javax.inject.Inject

class PuzzleRepositoryImpl @Inject constructor(
    private val puzzleDao: PuzzleDao
) : PuzzleRepository {

    override suspend fun getPuzzle(puzzleId: PuzzleId): TecResult<Puzzle> {
        return suspendRunCatching {
            val entity = puzzleDao.getById(puzzleId.value)
                ?: throw NoSuchElementException("Puzzle not found: ${puzzleId.value}")
            entity.toDomain()
        }.mapError { e ->
            when (e) {
                is NoSuchElementException -> AppError.Unknown(e.message ?: "Puzzle not found")
                else -> e.toAppError()
            }
        }
    }

    override suspend fun savePuzzle(puzzle: Puzzle): TecResult<Unit> {
        return suspendRunCatching {
            puzzleDao.insert(puzzle.toEntity())
        }
    }
}

private fun <T> TecResult<T>.mapError(transform: (Throwable) -> AppError): TecResult<T> = when (this) {
    is TecResult.Success -> this
    is TecResult.Error -> TecResult.Error(transform(error.toException()))
}