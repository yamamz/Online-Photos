package com.yamamz.photos.data.network

import com.yamamz.photos.data.network.model.ImageDTO
import retrofit2.http.GET

interface PhotoApiService {

    @GET("v2/list")
    suspend fun getPhotos(): List<ImageDTO>
}