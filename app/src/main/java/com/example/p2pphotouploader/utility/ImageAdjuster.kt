package com.example.p2pphotouploader.utility

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy

/**
 * A utility function that transforms the captured image from landscape to portrait
 * when using the CameraX library.
 *
 * @param [image]
 *      An imageProxy object
 */
fun imageAdjusterPortrait(image: ImageProxy): Bitmap {
    val matrix = Matrix().apply {
        postRotate(image.imageInfo.rotationDegrees.toFloat())
    }

    return Bitmap.createBitmap(
        image.toBitmap(),
        0,
        0,
        image.width,
        image.height,
        matrix,
        true
    )
}