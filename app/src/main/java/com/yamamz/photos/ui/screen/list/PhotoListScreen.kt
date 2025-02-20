package com.yamamz.photos.ui.screen.list

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.yamamz.photos.R
import com.yamamz.photos.core.Dimensions

import com.yamamz.photos.domain.model.Image
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun PhotoListScreenRoot(
    viewModel: PhotoViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        viewModel.downloadSuccess.collect { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    PhotoListScreen(
        photoListState = state,
        snackBarState = snackbarHostState,
        onImageTap = {
            val encodeUrl = Uri.encode(it.downloadUrl)
            navController.navigate("photo_detail/$encodeUrl/${it.author}")
        }, onDownload = { image ->
            viewModel.downloadImage(
                context,
                imageUrl = image.downloadUrl,
                fileName = "image_downloaded.jpg",
                id = image.id
            )
        })
}

@Composable
fun PhotoListScreen(
    photoListState: PhotoListState,
    onDownload: (Image) -> Unit,
    onImageTap: (Image) -> Unit,
    snackBarState: SnackbarHostState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { CustomSnackbar(snackBarState) }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(Dimensions.columnSize),
                verticalItemSpacing = Dimensions.staggeredGridSpacing,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.staggeredGridSpacing),
                modifier = Modifier.fillMaxSize()
            ) {
                items(photoListState.images) { image ->
                    val isImageDownloading =
                        photoListState.loadingImage?.isDownloadingImage == true && image.id == photoListState.loadingImage?.id
                    ImageBox(
                        image,
                        onImageTap = onImageTap,
                        onDownload = onDownload,
                        isImageDownloading
                    )
                }
            }

            if (photoListState.isLoading) CircularProgressIndicator(
                Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun ImageBox(
    image: Image,
    onImageTap: (Image) -> Unit,
    onDownload: (Image) -> Unit,
    isLoading: Boolean
) {
    val randomHeight = remember { Random.nextInt(Dimensions.imageHeightMin.value.toInt(), Dimensions.imageHeightMax.value.toInt()).dp }
    Box(
        Modifier.clickable { onImageTap.invoke(image) }
    ) {
        AsyncImage(
            model = image.downloadUrl,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(randomHeight)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.imageOverlayHeight)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                    )
                )
        )

        Text(
            text = image.author,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Dimensions.paddingLarge)
        )

        DownloadIconWithProgress(
            onDownload = { onDownload.invoke(image) },
            modifier = Modifier,
            isLoading = isLoading
        )
    }
}

@Composable
fun DownloadIconWithProgress(
    isLoading: Boolean,
    onDownload: () -> Unit,
    modifier: Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(Dimensions.progressSize)
            .padding(Dimensions.paddingMedium)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                strokeWidth = Dimensions.strokeWidth,
                color = Color.White,
                modifier = Modifier.matchParentSize()
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_download),
            contentDescription = "Download",
            tint = Color.White,
            modifier = modifier
                .size(Dimensions.iconSize)
                .clickable(enabled = !isLoading) { onDownload.invoke() }
        )
    }
}

@Composable
fun CustomSnackbar(snackbarHostState: SnackbarHostState) {
    SnackbarHost(hostState = snackbarHostState) { data ->
        Snackbar(
            modifier = Modifier.padding(Dimensions.snackbarPadding),
            containerColor = Color(0xFF323232),
            contentColor = Color.White,
            actionContentColor = Color.Cyan,
            shape = RoundedCornerShape(Dimensions.paddingMedium),
            action = {
                Text(
                    text = "OK",
                    color = Color.White,
                    modifier = Modifier.clickable { data.dismiss() }
                )
            }
        ) {
            Text(text = data.visuals.message)
        }
    }
}
