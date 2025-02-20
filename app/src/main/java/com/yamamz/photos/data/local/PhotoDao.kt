package com.yamamz.photos.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface PhotoDao {

    @Upsert
    suspend fun insertPhotos(images: List<ImageEntity>)

    @Query(
        """
            SELECT * 
            FROM imageentity
        """
    )
    suspend fun getAllPhotos(): List<ImageEntity>
}