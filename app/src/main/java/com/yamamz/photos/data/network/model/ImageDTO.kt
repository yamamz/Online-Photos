package com.yamamz.photos.data.network.model

import com.google.gson.annotations.SerializedName
import com.yamamz.photos.domain.model.Image
import kotlinx.serialization.Serializable

@Serializable
data class ImageDTO(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    @SerializedName("download_url")
    val downloadUrl: String? = null
)

fun ImageDTO.toDomain(): Image = Image(
    id = id,
    author = author,
    width = width,
    height = height,
    url = url,
    downloadUrl = downloadUrl.orEmpty()
)
