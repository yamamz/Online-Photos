package com.yamamz.photos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ImageEntity::class],
    version = 1
)
abstract class PhotoDatabase: RoomDatabase() {

    abstract val dao: PhotoDao
}