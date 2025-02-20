package com.yamamz.photos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yamamz.photos.domain.model.Image

@Entity
data class ImageEntity(
    @PrimaryKey val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    val downloadUrl: String
)

fun ImageEntity.toDomain(): Image = Image(
    id = id,
    author = author,
    width = width,
    height = height,
    url = url,
    downloadUrl = downloadUrl
)
