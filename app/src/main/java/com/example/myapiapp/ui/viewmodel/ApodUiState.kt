package com.example.myapiapp.ui.viewmodel

import com.example.myapiapp.data.remote.ApodItem

sealed interface ApodSectionUiState<out T> {
    data object Loading : ApodSectionUiState<Nothing>
    data class Success<T>(val data: T) : ApodSectionUiState<T>
    data class Error(val message: String) : ApodSectionUiState<Nothing>
}

data class HomeUiState(
    val heroItem: ApodItem? = null,
    val galleryState: ApodSectionUiState<List<ApodItem>> = ApodSectionUiState.Loading
)
