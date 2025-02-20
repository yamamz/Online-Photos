package com.yamamz.photos.data.repository

import com.yamamz.photos.domain.model.Image

interface PhotoRepository {
    suspend fun getPhotos(fromApi: Boolean = true): List<Image>
}