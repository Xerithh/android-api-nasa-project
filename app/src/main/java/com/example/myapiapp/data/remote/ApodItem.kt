package com.example.myapiapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApodItem(
    val date: String,
    val title: String = "",
    val explanation: String = "",
    val url: String = "",
    @SerialName("hdurl") val hdUrl: String? = null,
    @SerialName("media_type") val mediaType: String = "image",
    @SerialName("service_version") val serviceVersion: String? = null,
    val copyright: String? = null
) {
    val displayUrl: String
        get() = sanitizeUrl(url)

    val displayHdUrl: String
        get() = sanitizeUrl(hdUrl?.takeIf { it.isNotBlank() } ?: url)

    private fun sanitizeUrl(raw: String): String {
        if (raw.isBlank()) return ""
        val cleaned = raw
            .trim()
            .replace("&#038;", "&")
            .replace("&amp;", "&")
            .replace(" ", "%20")

        if (mediaType.equals("video", ignoreCase = true)) {
            val youtubeRegex = "(?:youtube\\.com\\/(?:[^/]+/.+/|(?:v|e(?:mbed)?)/|.*[?&]v=)|youtu\\.be/)([^\"&?/\\s]{11})".toRegex()
            val match = youtubeRegex.find(cleaned)
            if (match != null) {
                val videoId = match.groupValues[1]
                return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
            }
        }
        return if (cleaned.startsWith("http://")) {
            cleaned.replaceFirst("http://", "https://")
        } else {
            cleaned
        }
    }
}

@Serializable
data class NasaImageLibraryResponse(
    val collection: NasaCollectionDto = NasaCollectionDto()
)

@Serializable
data class NasaCollectionDto(
    val items: List<NasaItemDto> = emptyList()
)

@Serializable
data class NasaItemDto(
    val data: List<NasaItemDataDto> = emptyList(),
    val links: List<NasaItemLinkDto> = emptyList()
) {
    fun toApodItem(): ApodItem {
        val itemData = data.firstOrNull()
        val previewLink = links.firstOrNull { it.render == "image" || it.rel == "preview" } ?: links.firstOrNull()
        val rawDate = itemData?.dateCreated.orEmpty()
        val dateOnly = if (rawDate.length >= 10) rawDate.substring(0, 10) else rawDate

        val rawImgUrl = previewLink?.href.orEmpty().trim().replace("http://", "https://")
        val imgUrl = rawImgUrl.replace(" ", "%20")
        val mediumUrl = if (imgUrl.contains("~thumb.jpg")) {
            imgUrl.replace("~thumb.jpg", "~medium.jpg")
        } else {
            imgUrl
        }

        return ApodItem(
            date = if (dateOnly.isNotBlank()) dateOnly else (itemData?.nasaId ?: "NASA"),
            title = itemData?.title.orEmpty().ifBlank { "Photo spatiale NASA" },
            explanation = itemData?.description.orEmpty().ifBlank { "Aucune description disponible." },
            url = imgUrl,
            hdUrl = mediumUrl,
            mediaType = itemData?.mediaType.orEmpty().ifBlank { "image" },
            copyright = itemData?.photographer ?: itemData?.secondaryCreator ?: itemData?.center ?: "NASA"
        )
    }
}

@Serializable
data class NasaItemDataDto(
    val title: String? = null,
    val description: String? = null,
    @SerialName("date_created") val dateCreated: String? = null,
    @SerialName("media_type") val mediaType: String? = null,
    @SerialName("nasa_id") val nasaId: String? = null,
    val photographer: String? = null,
    @SerialName("secondary_creator") val secondaryCreator: String? = null,
    val center: String? = null
)

@Serializable
data class NasaItemLinkDto(
    val href: String? = null,
    val rel: String? = null,
    val render: String? = null
)
