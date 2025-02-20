package com.yamamz.photos.data.network

import com.yamamz.photos.domain.model.Image
import com.yamamz.photos.data.network.model.toDomain
import javax.inject.Inject

class PhotoRemoteSourceImpl @Inject constructor(
    private val photoApiService: PhotoApiService
): PhotoRemoteSource {
    override suspend fun getPhotos(): List<Image> {
        return photoApiService.getPhotos().map { it.toDomain() }
    }
}