package com.example.myapiapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CircularProgressIndicator
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.myapiapp.data.local.ApodEntity
import com.example.myapiapp.data.local.toItem
import com.example.myapiapp.data.remote.ApodItem
import com.example.myapiapp.ui.theme.MyAPIAppTheme
import com.example.myapiapp.ui.viewmodel.ApodViewModel

@Composable
fun FavoritesScreen(
    viewModel: ApodViewModel,
    onNavigateToDetail: (ApodItem) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    FavoritesContent(
        favorites = favorites,
        onNavigateToDetail = onNavigateToDetail,
        onRemoveFavorite = { date -> viewModel.removeFavorite(date) },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesContent(
    favorites: List<ApodEntity>,
    onNavigateToDetail: (ApodItem) -> Unit,
    onRemoveFavorite: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mes Favoris (${favorites.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aucun favori enregistré",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ajoutez des images en favoris depuis l'écran principal ou la vue détaillée.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 340.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites, key = { it.date }) { favEntity ->
                    val item = favEntity.toItem()
                    FavoriteCard(
                        item = item,
                        onCardClick = { onNavigateToDetail(item) },
                        onRemoveFavorite = { onRemoveFavorite(item.date) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteCard(
    item: ApodItem,
    onCardClick: () -> Unit,
    onRemoveFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.displayUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onRemoveFavorite) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer des favoris",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// ================= PREVIEWS =================

private val previewFavoriteEntities = listOf(
    ApodEntity(
        date = "2024-05-15",
        title = "The Pillars of Creation",
        explanation = "Eagle Nebula dusty gas towers.",
        url = "https://apod.nasa.gov/apod/image/2405/Pillars_Hubble_960.jpg",
        hdUrl = null,
        mediaType = "image",
        copyright = "NASA"
    ),
    ApodEntity(
        date = "2024-05-14",
        title = "Andromeda Galaxy M31",
        explanation = "Spiral galaxy neighbor.",
        url = "https://apod.nasa.gov/apod/image/2405/Andromeda_960.jpg",
        hdUrl = null,
        mediaType = "image",
        copyright = "ESA"
    )
)

@Preview(showBackground = true, name = "Favorites Screen - Populated")
@Composable
fun FavoritesScreenPreview() {
    MyAPIAppTheme {
        FavoritesContent(
            favorites = previewFavoriteEntities,
            onNavigateToDetail = {},
            onRemoveFavorite = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Favorites Screen - Dark Mode")
@Composable
fun FavoritesScreenDarkPreview() {
    MyAPIAppTheme {
        FavoritesContent(
            favorites = previewFavoriteEntities,
            onNavigateToDetail = {},
            onRemoveFavorite = {},
            onBackClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240",
    name = "Favorites Screen - Tablet/PC Landscape"
)
@Composable
fun FavoritesScreenTabletPreview() {
    MyAPIAppTheme {
        FavoritesContent(
            favorites = previewFavoriteEntities,
            onNavigateToDetail = {},
            onRemoveFavorite = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Favorites Screen - Empty")
@Composable
fun FavoritesScreenEmptyPreview() {
    MyAPIAppTheme {
        FavoritesContent(
            favorites = emptyList(),
            onNavigateToDetail = {},
            onRemoveFavorite = {},
            onBackClick = {}
        )
    }
}
