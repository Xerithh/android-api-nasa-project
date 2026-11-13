package com.example.myapiapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapiapp.data.remote.ApodItem
import com.example.myapiapp.ui.components.ApodGridItemCard
import com.example.myapiapp.ui.components.ApodHeroCard
import com.example.myapiapp.ui.components.ErrorView
import com.example.myapiapp.ui.components.LoadingView
import com.example.myapiapp.ui.theme.MyAPIAppTheme
import com.example.myapiapp.ui.viewmodel.ApodSectionUiState
import com.example.myapiapp.ui.viewmodel.ApodViewModel
import com.example.myapiapp.ui.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    viewModel: ApodViewModel,
    onNavigateToDetail: (ApodItem) -> Unit,
    onNavigateToFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeState by viewModel.homeState.collectAsStateWithLifecycle()

    HomeScreenContent(
        homeState = homeState,
        isFavorite = { date ->
            val favState by viewModel.isFavoriteFlow(date).collectAsStateWithLifecycle(initialValue = false)
            favState
        },
        onRefresh = { viewModel.refresh() },
        onNavigateToFavorites = onNavigateToFavorites,
        onToggleFavorite = { item -> viewModel.toggleFavorite(item) },
        onNavigateToDetail = onNavigateToDetail,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    homeState: HomeUiState,
    isFavorite: @Composable (String) -> Boolean,
    onRefresh: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onToggleFavorite: (ApodItem) -> Unit,
    onNavigateToDetail: (ApodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NASA Space Gallery",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Exploration spatiale",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rafraîchir"
                        )
                    }
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favoris",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val screenWidth = maxWidth
            val isExpandedScreen = screenWidth >= 600.dp

            if (isExpandedScreen) {
                // Layout 2 colonnes pour Tablettes, PC et Mode Paysage
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Côté GAUCHE (50% fixe) : Image principale (Hero Card)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "À la une",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val hero = homeState.heroItem
                        if (hero != null) {
                            val isFav = isFavorite(hero.date)
                            ApodHeroCard(
                                item = hero,
                                isFavorite = isFav,
                                onCardClick = { onNavigateToDetail(hero) },
                                onFavoriteToggle = { onToggleFavorite(hero) },
                                showDescription = true,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                LoadingView(message = "Chargement de l'image du jour...")
                            }
                        }
                    }

                    // Côté DROIT (50%) : Conteneur Lazy de défilement pour les autres images
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "Galerie photos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val columnsCount = if (screenWidth >= 1000.dp) 3 else 2
                        when (val gallery = homeState.galleryState) {
                            is ApodSectionUiState.Loading -> {
                                LoadingView(message = "Chargement des images...")
                            }
                            is ApodSectionUiState.Error -> {
                                ErrorView(
                                    message = gallery.message,
                                    onRetry = onRefresh
                                )
                            }
                            is ApodSectionUiState.Success -> {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(columnsCount),
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(gallery.data, key = { "${it.date}_${it.url}" }) { item ->
                                        ApodGridItemCard(
                                            item = item,
                                            onCardClick = { onNavigateToDetail(item) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Layout standard 1 colonne globale pour Smartphones en mode Portrait
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val hero = homeState.heroItem
                    if (hero != null) {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = "À la une",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }

                        item(span = { GridItemSpan(2) }) {
                            val isFav = isFavorite(hero.date)
                            ApodHeroCard(
                                item = hero,
                                isFavorite = isFav,
                                onCardClick = { onNavigateToDetail(hero) },
                                onFavoriteToggle = { onToggleFavorite(hero) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            )
                        }
                    }

                    item(span = { GridItemSpan(2) }) {
                        Text(
                            text = "Galerie photos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    when (val gallery = homeState.galleryState) {
                        is ApodSectionUiState.Loading -> {
                            item(span = { GridItemSpan(2) }) {
                                LoadingView(message = "Chargement des images de la NASA...")
                            }
                        }
                        is ApodSectionUiState.Error -> {
                            item(span = { GridItemSpan(2) }) {
                                ErrorView(
                                    message = gallery.message,
                                    onRetry = onRefresh
                                )
                            }
                        }
                        is ApodSectionUiState.Success -> {
                            items(gallery.data, key = { "${it.date}_${it.url}" }) { item ->
                                ApodGridItemCard(
                                    item = item,
                                    onCardClick = { onNavigateToDetail(item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= PREVIEWS =================

private val previewSampleNasaItem = ApodItem(
    date = "2024-05-15",
    title = "Les Piliers de la Création",
    explanation = "Piliers de poussière interstellaire capturés par le télescope spatial.",
    url = "https://images-assets.nasa.gov/image/PIA25433/PIA25433~thumb.jpg",
    hdUrl = "https://images-assets.nasa.gov/image/PIA25433/PIA25433~medium.jpg",
    mediaType = "image",
    copyright = "NASA / ESA"
)

private val previewNasaGallery = listOf(
    ApodItem(
        date = "2024-05-14",
        title = "Galaxie d'Andromède M31",
        explanation = "Vue spectaculaire de la galaxie spirale.",
        url = "https://images-assets.nasa.gov/image/PIA15416/PIA15416~thumb.jpg"
    ),
    ApodItem(
        date = "2024-05-13",
        title = "Rover Perseverance sur Mars",
        explanation = "Exploration du cratère Jezero.",
        url = "https://images-assets.nasa.gov/image/PIA24424/PIA24424~thumb.jpg"
    ),
    ApodItem(
        date = "2024-05-12",
        title = "Aurores boréales de Jupiter",
        explanation = "Aurores polaires géantes sur Jupiter.",
        url = "https://images-assets.nasa.gov/image/PIA20700/PIA20700~thumb.jpg"
    ),
    ApodItem(
        date = "2024-05-11",
        title = "Nébuleuse d'Orion",
        explanation = "Pépinière d'étoiles dans Orion.",
        url = "https://images-assets.nasa.gov/image/PIA08013/PIA08013~thumb.jpg"
    )
)

@Preview(showBackground = true, name = "NASA Gallery - Light Mode")
@Composable
fun HomeScreenPreview() {
    MyAPIAppTheme {
        HomeScreenContent(
            homeState = HomeUiState(
                heroItem = previewSampleNasaItem,
                galleryState = ApodSectionUiState.Success(previewNasaGallery)
            ),
            isFavorite = { true },
            onRefresh = {},
            onNavigateToFavorites = {},
            onToggleFavorite = {},
            onNavigateToDetail = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "NASA Gallery - Dark Mode")
@Composable
fun HomeScreenDarkPreview() {
    MyAPIAppTheme {
        HomeScreenContent(
            homeState = HomeUiState(
                heroItem = previewSampleNasaItem,
                galleryState = ApodSectionUiState.Success(previewNasaGallery)
            ),
            isFavorite = { false },
            onRefresh = {},
            onNavigateToFavorites = {},
            onToggleFavorite = {},
            onNavigateToDetail = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240",
    name = "NASA Gallery - Tablet/PC Landscape"
)
@Composable
fun HomeScreenTabletPreview() {
    MyAPIAppTheme {
        HomeScreenContent(
            homeState = HomeUiState(
                heroItem = previewSampleNasaItem,
                galleryState = ApodSectionUiState.Success(previewNasaGallery)
            ),
            isFavorite = { true },
            onRefresh = {},
            onNavigateToFavorites = {},
            onToggleFavorite = {},
            onNavigateToDetail = {}
        )
    }
}
