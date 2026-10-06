package dev.fbaldhagen.tectonic.common

import kotlin.coroutines.cancellation.CancellationException

sealed interface TecResult<out T> {
    data class Success<T>(val data: T) : TecResult<T>
    data class Error(val error: AppError) : TecResult<Nothing>
}

inline fun <T> TecResult<T>.onSuccess(action: (T) -> Unit): TecResult<T> {
    if (this is TecResult.Success) action(data)
    return this
}

inline fun <T> TecResult<T>.onError(action: (AppError) -> Unit): TecResult<T> {
    if (this is TecResult.Error) action(error)
    return this
}

fun <T> TecResult<T>.getOrNull(): T? = (this as? TecResult.Success)?.data

fun <T> TecResult<T>.getOrThrow(): T = when (this) {
    is TecResult.Success -> data
    is TecResult.Error -> throw error.toException()
}

inline fun <T, R> TecResult<T>.map(transform: (T) -> R): TecResult<R> = when (this) {
    is TecResult.Success -> TecResult.Success(transform(data))
    is TecResult.Error -> this
}

inline fun <T, R> TecResult<T>.flatMap(transform: (T) -> TecResult<R>): TecResult<R> = when (this) {
    is TecResult.Success -> transform(data)
    is TecResult.Error -> this
}

inline fun <T> TecResult<T>.getOrDefault(defaultValue: () -> T): T = when (this) {
    is TecResult.Success -> data
    is TecResult.Error -> defaultValue()
}

suspend fun <T> suspendRunCatching(block: suspend () -> T): TecResult<T> =
    try {
        TecResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        TecResult.Error(e.toAppError())
    }

fun <T> runCatching(block: () -> T): TecResult<T> =
    try {
        TecResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        TecResult.Error(e.toAppError())
    }