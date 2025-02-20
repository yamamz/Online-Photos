package com.yamamz.photos.ui.screen.list

import android.content.Context
import com.yamamz.photos.domain.model.Image
import com.yamamz.photos.domain.usecase.GetPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.yamamz.photos.core.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


@HiltViewModel
class PhotoViewModel @Inject constructor(private val photosUseCase: GetPhotosUseCase) :
    ViewModel() {

    private val _state = mutableStateOf(PhotoListState())
    val state: State<PhotoListState> = _state
    private val _downloadSuccess = MutableSharedFlow<String>() // Emits messages
    val downloadSuccess = _downloadSuccess.asSharedFlow()

    init {
        getPhotos()
    }

    private fun getPhotos() {
        photosUseCase(fromApi = true).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value  = _state.value.copy(isLoading = false, images = result.data?.shuffled().orEmpty())
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false,
                        error = result.message.orEmpty()
                    )
                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun downloadImage(context: Context, imageUrl: String, fileName: String, id: String) {
        viewModelScope.launch {
            _state.value  = state.value.copy(loadingImage = LoadingImage(id = id, isDownloadingImage = true))
            Utils.downloadImage(context, imageUrl, fileName)
            _state.value  = state.value.copy(loadingImage = LoadingImage(id = id, isDownloadingImage = false))
            _downloadSuccess.emit("Download complete!")
        }
    }
}



data class PhotoListState(
    val isLoading: Boolean = false,
    val loadingImage: LoadingImage? = null,
    val images: List<Image> = emptyList(),
    val error: String = ""
)

data class LoadingImage(
    val id: String,
    val isDownloadingImage: Boolean
)
