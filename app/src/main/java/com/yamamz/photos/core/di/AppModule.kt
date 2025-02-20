package com.yamamz.photos.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.yamamz.photos.core.Constants
import com.yamamz.photos.data.repository.PhotoRepository
import com.yamamz.photos.data.repository.PhotoRepositoryImpl
import com.yamamz.photos.data.local.PhotoDatabase
import com.yamamz.photos.data.network.PhotoApiService
import com.yamamz.photos.data.network.PhotoRemoteSource
import com.yamamz.photos.data.network.PhotoRemoteSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }

    @Provides
    @Singleton
    fun providesPhotoApi(): PhotoApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhotoApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesPhotoRepository(remote: PhotoRemoteSource, photoDatabase: PhotoDatabase) : PhotoRepository {
        return PhotoRepositoryImpl(remote, photoDatabase)
    }

    @Provides
    @Singleton
    fun providesPhotoRemote(api: PhotoApiService): PhotoRemoteSource {
        return PhotoRemoteSourceImpl(api)
    }

    @Provides
    @Singleton
    fun providesDatabase(app: Application) : PhotoDatabase {
        return Room.databaseBuilder(
            app,
            PhotoDatabase:: class.java,
            "photodb.db"
        ).build()
    }
}