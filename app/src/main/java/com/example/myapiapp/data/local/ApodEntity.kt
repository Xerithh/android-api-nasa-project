package com.example.myapiapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapiapp.data.remote.ApodItem

@Entity(tableName = "apod_favorites")
data class ApodEntity(
    @PrimaryKey
    val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdUrl: String?,
    val mediaType: String,
    val copyright: String?,
    val savedAt: Long = System.currentTimeMillis()
)

fun ApodItem.toEntity(): ApodEntity {
    return ApodEntity(
        date = date,
        title = title,
        explanation = explanation,
        url = url,
        hdUrl = hdUrl,
        mediaType = mediaType,
        copyright = copyright
    )
}

fun ApodEntity.toItem(): ApodItem {
    return ApodItem(
        date = date,
        title = title,
        explanation = explanation,
        url = url,
        hdUrl = hdUrl,
        mediaType = mediaType,
        serviceVersion = "v1",
        copyright = copyright
    )
}
