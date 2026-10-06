package dev.fbaldhagen.tectonic.common

sealed interface AppError {

    val message: String

    // region Network
    data class Network(
        override val message: String = "Network error",
        val cause: Throwable? = null
    ) : AppError

    data object NoInternet : AppError {
        override val message: String = "No internet connection"
    }
    // endregion

    // region Database
    data class Database(
        override val message: String = "Database error",
        val cause: Throwable? = null
    ) : AppError
    // endregion

    // region Game / Puzzle
    data class CellNotFound(
        val row: Int,
        val column: Int,
        override val message: String = "Cell not found"
    ) : AppError

    data class CellNotEditable(
        val row: Int,
        val column: Int,
        override val message: String = "Cell is not editable"
    ) : AppError

    data class InvalidMove(
        val violations: List<String>,          // or keep a simple description list
        override val message: String = "Invalid move"
    ) : AppError

    data object NothingToUndo : AppError {
        override val message: String = "Nothing to undo"
    }
    // endregion

    // region Auth-specific
    data object InvalidCredentials : AppError {
        override val message: String = "Incorrect email or password"
    }

    data object EmailAlreadyRegistered : AppError {
        override val message: String = "An account with this email already exists"
    }
    // endregion

    // region Generic
    data class Unknown(
        override val message: String = "An unexpected error occurred",
        val cause: Throwable? = null
    ) : AppError
    // endregion
}

fun AppError.toException(): Exception = when (this) {
    is AppError.Network -> Exception(message, cause)
    is AppError.NoInternet -> Exception(message)
    is AppError.Database -> Exception(message, cause)
    is AppError.CellNotFound -> Exception(message)
    is AppError.CellNotEditable -> Exception(message)
    is AppError.InvalidMove -> Exception(message)
    is AppError.NothingToUndo -> Exception(message)
    is AppError.InvalidCredentials -> Exception(message)
    is AppError.EmailAlreadyRegistered -> Exception(message)
    is AppError.Unknown -> Exception(message, cause)
}

fun Throwable.toAppError(): AppError = when (this) {
    is java.net.UnknownHostException -> AppError.NoInternet
    is java.net.SocketTimeoutException -> AppError.Network("Connection timed out", this)
    else -> AppError.Unknown(message ?: "An unexpected error occurred", this)
}