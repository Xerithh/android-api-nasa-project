package com.example.myapiapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(entity: ApodEntity)

    @Delete
    suspend fun deleteFavorite(entity: ApodEntity)

    @Query("DELETE FROM apod_favorites WHERE date = :date")
    suspend fun deleteFavoriteByDate(date: String)

    @Query("SELECT * FROM apod_favorites ORDER BY savedAt DESC")
    fun getAllFavorites(): Flow<List<ApodEntity>>

    @Query("SELECT * FROM apod_favorites WHERE date = :date LIMIT 1")
    suspend fun getFavoriteByDate(date: String): ApodEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM apod_favorites WHERE date = :date)")
    fun isFavoriteFlow(date: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM apod_favorites WHERE date = :date)")
    suspend fun isFavorite(date: String): Boolean
}
