package com.example.p2pphotouploader.ui

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.impl.utils.MatrixExt.postRotate
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PanoramaFishEye
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.p2pphotouploader.R

/**
 * This composable is responsible for capturing photos and saving it as
 * a bitmap to the [TargetUiState].
 *
 * @param [modifier]
 *      The modifier object
 *
 * @param [onPhotoTaken]
 *      A lambda function from viewModel to update the TargetUIState's
 *      image property with the image taken
 *
 * @param [toNextScreen]
 *      A lambda function that routes the user to the
 *      next (configuration) screen
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCaptureScreen(modifier: Modifier = Modifier,
                       onPhotoTaken: (Bitmap) -> Unit = {},
                       toNextScreen: () -> Unit = {}
) {
    // Initialize variables
    val context = LocalContext.current
    val scaffoldState = rememberBottomSheetScaffoldState()
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }
    var isPhotoError by rememberSaveable { mutableStateOf(false) }

    BottomSheetScaffold(
        sheetPeekHeight = 0.dp,
        scaffoldState = scaffoldState,
        sheetContent = {

        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CameraPreview(
                controller = controller,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                modifier = Modifier.offset(16.dp, 16.dp),
                onClick = { switchCameraHandler(controller) }
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = stringResource(R.string.switch_camera_icon)
                )
            }

            // Bottom Bar of Camera Feed           
            Row(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(150.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    modifier = Modifier.size(100.dp),
                    onClick = {
                        if(!takePhotoHandler(context, controller, onPhotoTaken)) {
                            toNextScreen()
                        } else {
                            isPhotoError = true
                        }
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(100.dp),
                        imageVector = Icons.Default.Camera,
                        contentDescription = stringResource(R.string.take_photo),
                    )
                }
            }
        }
        if(isPhotoError) {
            PhotoCaptureErrorDialog(context = context)
        }
    }
}


@Composable
fun PhotoCaptureErrorDialog(context: Context) {
    AlertDialog(
        icon = {
            Icon(
                Icons.Default.Error,
                contentDescription = stringResource(R.string.error_icon)
            )
        },
        title = {
            Text(text = stringResource(R.string.photo_capture_error))
        },
        text = {
            Text(text = stringResource(R.string.photo_capture_error_desc))
        },
        onDismissRequest = { closeApplication(context) },
        confirmButton = {
            TextButton(
                onClick = {
                    closeApplication(context)
                }
            ) {
                Text(stringResource(R.string.exit_dialog))
            }
        },
    )
}


@Composable
fun CameraPreview(controller: LifecycleCameraController,
                  modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        })
}


private fun takePhotoHandler(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit = {}) : Boolean
{
    var isError = false
    controller.takePicture(
        ContextCompat.getMainExecutor(context),

        object: OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                // Process image to portrait
                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTaken(rotatedBitmap)
                isError = false
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("CAMERA", context.getString(R.string.capture_photo_error), exception)
                isError = true
            }
        }
    )
    return isError
}


private fun switchCameraHandler(controller: LifecycleCameraController) {
    controller.cameraSelector =
        if(controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
}


private fun closeApplication(context: Context) {
    val activity = context as Activity
    activity.finish()
}