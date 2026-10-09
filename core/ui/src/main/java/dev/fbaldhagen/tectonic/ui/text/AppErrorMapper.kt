package dev.fbaldhagen.tectonic.ui.text

import dev.fbaldhagen.tectonic.common.AppError
import dev.fbaldhagen.tectonic.ui.R

/**
 * Converts a domain [AppError] into a [UiText] ready for display.
 *
 * This `when` is exhaustive over [AppError]'s sealed hierarchy with no `else`
 * branch - if a new AppError variant is added in `core:common`, this file
 * fails to compile until it's handled here. That's intentional: it's the one
 * place that guarantees every error has a defined user-facing message.
 *
 * Errors with a fixed, known message are mapped to real string resources
 * (localizable). Errors that wrap dynamic content - e.g. an exception message
 * that varies at runtime - fall back to [UiText.Dynamic] using the message
 * already produced in `core:common`.
 */
fun AppError.toUiText(): UiText = when (this) {
    is AppError.CellNotEditable -> UiText.Resource(R.string.error_cell_not_editable)
    is AppError.CellNotFound -> UiText.Resource(R.string.error_cell_not_found)
    is AppError.EmailAlreadyRegistered -> UiText.Resource(R.string.error_already_registered)
    is AppError.InvalidCredentials -> UiText.Resource(R.string.error_invalid_credentials)
    is AppError.InvalidMove -> UiText.Resource(R.string.error_invalid_move)
    is AppError.NoInternet -> UiText.Resource(R.string.error_no_internet)
    is AppError.NothingToUndo -> UiText.Resource(R.string.error_nothing_to_undo)
    is AppError.GameNotInProgress -> UiText.Resource(R.string.error_game_not_in_progress)

    // These carry a message that's already dynamic (e.g. built from an
    // exception) at the point AppError was created - no fixed resource to map to.
    is AppError.Network -> UiText.Dynamic(message)
    is AppError.Database -> UiText.Dynamic(message)
    is AppError.Unknown -> UiText.Dynamic(message)
}