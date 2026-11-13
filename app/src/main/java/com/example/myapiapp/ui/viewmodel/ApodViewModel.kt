package com.example.myapiapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapiapp.data.local.ApodEntity
import com.example.myapiapp.data.remote.ApodItem
import com.example.myapiapp.data.repository.ApodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ApodViewModel(
    private val repository: ApodRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    val favorites: StateFlow<List<ApodEntity>> = repository.getFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        loadPhotos()
    }

    fun refresh() {
        loadPhotos()
    }

    fun loadPhotos(query: String = "space") {
        _homeState.update {
            it.copy(
                galleryState = ApodSectionUiState.Loading
            )
        }
        viewModelScope.launch {
            repository.searchNasaImages(query = query, count = 30)
                .onSuccess { items ->
                    val hero = items.firstOrNull()
                    val gallery = if (items.size > 1) items.drop(1) else items
                    _homeState.update {
                        it.copy(
                            heroItem = hero,
                            galleryState = ApodSectionUiState.Success(gallery)
                        )
                    }
                }
                .onFailure { error ->
                    _homeState.update {
                        it.copy(
                            heroItem = null,
                            galleryState = ApodSectionUiState.Error(
                                error.localizedMessage ?: "Impossible de charger les images de la NASA"
                            )
                        )
                    }
                }
        }
    }

    fun toggleFavorite(item: ApodItem) {
        viewModelScope.launch {
            repository.toggleFavorite(item)
        }
    }

    fun removeFavorite(date: String) {
        viewModelScope.launch {
            repository.removeFavorite(date)
        }
    }

    fun isFavoriteFlow(date: String) = repository.isFavoriteFlow(date)

    companion object {
        fun provideFactory(repository: ApodRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ApodViewModel::class.java)) {
                        return ApodViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}
