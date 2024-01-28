package com.example.p2pphotouploader.data

/**
 * Data class that represents the current UI state in terms of [ipAddress]
 */
data class TargetUiState(
    val ipAddress: String = "",
    val targetPort: Int = 0,
    // TODO: Add a state for photo
)
