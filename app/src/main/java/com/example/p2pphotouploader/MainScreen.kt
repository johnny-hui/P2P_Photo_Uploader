package com.example.p2pphotouploader

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.p2pphotouploader.ui.PhotoCaptureScreen
import com.example.p2pphotouploader.ui.SplashScreen
import com.example.p2pphotouploader.ui.TargetViewModel

enum class P2PAppScreen {
    START,
    TAKE_PHOTO,
    CONFIGURATION,
    PREVIEW_PHOTO,
}


@Composable
fun P2PApp(modifier: Modifier = Modifier,
           viewModel: TargetViewModel = viewModel(),
           navController: NavHostController = rememberNavController()
) {
    // Locks the activity to constraint device UI rotation
    val activity = LocalContext.current as Activity
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

    // Initialize UI State from viewModel
    val targetUIState by viewModel.uiState.collectAsState()

    // NavHost Container (defined routes)
    NavHost(
        navController = navController,
        startDestination = P2PAppScreen.START.name
    ) {

        // a) To Splash Screen
        composable(route = P2PAppScreen.START.name) {
            SplashScreen(
                Modifier.background(color = Color.Black),
                onGetStartedClicked = {
                    navController.navigate(P2PAppScreen.TAKE_PHOTO.name)
                }
            )
        }

        // b) To Camera Photo Capture Screen
        composable(route = P2PAppScreen.TAKE_PHOTO.name) {
            PhotoCaptureScreen(
                modifier = Modifier.fillMaxSize(),
                onPhotoTaken = { viewModel.setPhotoTaken(bitmap = null) },
                toNextScreen = { navController.navigate(P2PAppScreen.START.name) } // Replace for test
            )
        }
    }
}
