package com.example.myapiapp.ui.navigation

import com.example.myapiapp.data.remote.ApodItem
import kotlinx.serialization.Serializable

sealed interface Destination

@Serializable
data object HomeDestination : Destination

@Serializable
data class DetailDestination(
    val item: ApodItem
) : Destination

@Serializable
data object FavoritesDestination : Destination
