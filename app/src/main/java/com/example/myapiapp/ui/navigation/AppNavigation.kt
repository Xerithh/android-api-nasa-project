package com.example.myapiapp.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.*
import com.example.myapiapp.ui.screens.DetailScreen
import com.example.myapiapp.ui.screens.FavoritesScreen
import com.example.myapiapp.ui.screens.HomeScreen
import com.example.myapiapp.ui.viewmodel.ApodViewModel

@Composable
fun AppNavigation(
    viewModel: ApodViewModel,
    modifier: Modifier = Modifier
) {
    val backStack = remember { mutableStateListOf<Any>(HomeDestination) }

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeLastOrNull()
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeDestination> {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { item ->
                        backStack.add(DetailDestination(item))
                    },
                    onNavigateToFavorites = {
                        backStack.add(FavoritesDestination)
                    }
                )
            }
            entry<DetailDestination> { destination ->
                DetailScreen(
                    item = destination.item,
                    viewModel = viewModel,
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<FavoritesDestination> {
                FavoritesScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { item ->
                        backStack.add(DetailDestination(item))
                    },
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        },
        modifier = modifier
    )
}
