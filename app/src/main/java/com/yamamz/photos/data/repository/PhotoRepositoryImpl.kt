package com.yamamz.photos.data.repository

import com.yamamz.photos.domain.model.toDB
import com.yamamz.photos.data.local.PhotoDatabase
import com.yamamz.photos.data.local.toDomain
import com.yamamz.photos.data.network.PhotoRemoteSource
import com.yamamz.photos.domain.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val photoRemoteSource: PhotoRemoteSource,
    photoDatabase: PhotoDatabase
) : PhotoRepository {
    private val dao = photoDatabase.dao

    override suspend fun getPhotos(fromApi: Boolean) = withContext(Dispatchers.IO) {
        if (fromApi) {
            // Insert data from server to local cache
            dao.insertPhotos(photoRemoteSource.getPhotos().map { it.toDB() })
            dao.getAllPhotos().map { it.toDomain() }.toList()
        }
        // Return  data from ca che
        dao.getAllPhotos().map { it.toDomain() }.toList()
    }
}