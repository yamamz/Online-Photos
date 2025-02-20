package com.yamamz.photos.domain.model

import com.yamamz.photos.data.local.ImageEntity

data class Image(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    val downloadUrl: String
)

fun Image.toDB(): ImageEntity = ImageEntity(
    id = id,
    author = author,
    width = width,
    height = height,
    url = url,
    downloadUrl = downloadUrl
)
