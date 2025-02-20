package com.yamamz.photos.domain.usecase

import com.yamamz.photos.core.Resource
import com.yamamz.photos.domain.PhotoRepository
import com.yamamz.photos.domain.model.Image
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import retrofit2.HttpException
import java.io.IOException

class GetPhotosUseCase @Inject constructor(
    private val repository: PhotoRepository
) {
    operator fun invoke(fromApi:Boolean): Flow<Resource<List<Image>>> = flow {
        try {
            if(fromApi) {
                emit(Resource.Loading())
            }
            val tracks = repository.getPhotos(fromApi = fromApi)
            emit(Resource.Success(tracks))
        } catch(e: HttpException) {
            val tracks = repository.getPhotos(fromApi = false)

            if(tracks.isEmpty()) {
                emit(Resource.Error(e.localizedMessage.orEmpty()))
            }else{
                emit(Resource.Success(tracks))
            }

        } catch(e: IOException) {
            val tracks = repository.getPhotos(fromApi = false)
            if(tracks.isEmpty()) {
                emit(Resource.Error(NO_INTERNET_ERROR))
            }else{
                emit(Resource.Success(tracks))
            }
        }
    }

    companion object {

        private const val NO_INTERNET_ERROR = "Couldn't reach the server. Check your internet connection."

    }
}
