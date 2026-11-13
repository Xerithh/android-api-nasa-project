package com.example.myapiapp.data.repository

import com.example.myapiapp.data.local.ApodDao
import com.example.myapiapp.data.local.ApodEntity
import com.example.myapiapp.data.local.toEntity
import com.example.myapiapp.data.remote.ApodItem
import com.example.myapiapp.data.remote.KtorClientProvider
import com.example.myapiapp.data.remote.NasaImageLibraryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.takeFrom
import kotlinx.coroutines.flow.Flow

class ApodRepository(
    private val client: HttpClient = KtorClientProvider.client,
    private val dao: ApodDao
) {
    companion object {
        private const val NASA_IMAGES_API_URL = "https://images-api.nasa.gov/search"
    }

    suspend fun searchNasaImages(query: String = "space", count: Int = 20): Result<List<ApodItem>> {
        return runCatching {
            val cleanQuery = query.ifBlank { "space" }
            val response: NasaImageLibraryResponse = client.get {
                url {
                    takeFrom(NASA_IMAGES_API_URL)
                    parameters.append("q", cleanQuery)
                    parameters.append("media_type", "image")
                }
            }.body()

            val items = response.collection.items
                .map { it.toApodItem() }
                .filter { it.url.isNotBlank() }

            if (items.isEmpty()) {
                throw NoSuchElementException("Aucune image trouvée pour : $cleanQuery")
            }
            items.take(count)
        }
    }

    fun getFavorites(): Flow<List<ApodEntity>> {
        return dao.getAllFavorites()
    }

    fun isFavoriteFlow(date: String): Flow<Boolean> {
        return dao.isFavoriteFlow(date)
    }

    suspend fun isFavorite(date: String): Boolean {
        return dao.isFavorite(date)
    }

    suspend fun toggleFavorite(item: ApodItem): Boolean {
        val exists = dao.isFavorite(item.date)
        if (exists) {
            dao.deleteFavoriteByDate(item.date)
            return false
        } else {
            dao.insertFavorite(item.toEntity())
            return true
        }
    }

    suspend fun removeFavorite(date: String) {
        dao.deleteFavoriteByDate(date)
    }
}
