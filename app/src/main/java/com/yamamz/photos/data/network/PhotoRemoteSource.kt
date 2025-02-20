package com.yamamz.photos.data.network

import com.yamamz.photos.domain.model.Image

interface PhotoRemoteSource {

    suspend fun getPhotos(): List<Image>
}