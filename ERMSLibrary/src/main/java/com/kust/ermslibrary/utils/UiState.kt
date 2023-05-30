package com.kust.ermslibrary.utils

sealed class UiState<out T> {
    object Loading: UiState<Nothing>()
    data class Success<out T>(val data: T): UiState<T>()
    data class Error(val error: String?): UiState<Nothing>()
}
