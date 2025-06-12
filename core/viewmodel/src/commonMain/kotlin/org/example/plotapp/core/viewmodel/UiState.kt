package org.example.plotapp.core.viewmodel

interface UiState

sealed interface CommonUiState<out T> : UiState {

    data object Loading : CommonUiState<Nothing>

    data class Content<T>(val data: T) : CommonUiState<T>

    data class Error(val message: String) : CommonUiState<Nothing>
}

inline fun <T> CommonUiState<T>.handle(
    onLoading: () -> Unit = {},
    onContent: (T) -> Unit = {},
    onError: (String) -> Unit = {},
) {
    when (this) {
        is CommonUiState.Loading -> onLoading()
        is CommonUiState.Content -> onContent(data)
        is CommonUiState.Error -> onError(message)
    }
}

inline fun <T, R> CommonUiState<T>.map(transform: (T) -> R): CommonUiState<R> {
    return when (this) {
        is CommonUiState.Loading -> CommonUiState.Loading
        is CommonUiState.Content -> CommonUiState.Content(transform(data))
        is CommonUiState.Error -> CommonUiState.Error(message)
    }
}