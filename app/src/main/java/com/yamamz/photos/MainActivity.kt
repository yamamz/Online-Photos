package com.yamamz.photos

import PhotoDetailScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yamamz.photos.ui.screen.list.PhotoListScreenRoot
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "photo_list") {
                composable("photo_list") {
                    PhotoListScreenRoot(navController = navController)
                }
                composable("photo_detail/{imageUrl}/{author}") { backStackEntry ->
                    val imageUrl = backStackEntry.arguments?.getString("imageUrl").orEmpty()
                    val author = backStackEntry.arguments?.getString("author").orEmpty()
                    AnimatedDetailScreen(imageUrl, author)
                }
            }
        }
    }
}

    @Composable
    fun AnimatedDetailScreen(imageUrl: String, author: String) {
        var isVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            isVisible = true
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
        ) {
            PhotoDetailScreen(imageUrl, author)
        }
    }

