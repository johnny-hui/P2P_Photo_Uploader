package com.example.p2pphotouploader.data

import android.graphics.Bitmap

/**
 * Data class that represents the current UI state in terms of [ipAddress]
 */
data class TargetUiState(
    val ipAddress: String = "",
    val targetPort: Int = 0,
    val capturedImage: Bitmap? = null
)
