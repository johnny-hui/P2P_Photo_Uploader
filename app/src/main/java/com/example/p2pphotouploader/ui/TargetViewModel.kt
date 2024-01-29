package com.example.p2pphotouploader.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.p2pphotouploader.data.TargetUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TargetViewModel : ViewModel() {
    /**
     * Exposes UI state to UI components and viewModel
     */
    private val _uiState = MutableStateFlow(TargetUiState())
    val uiState: StateFlow<TargetUiState> = _uiState.asStateFlow()

    // Setters (to UI state)
    fun setPhotoTaken(bitmap: Bitmap?) {
        _uiState.update { currentState ->
            currentState.copy(
                capturedImage = bitmap
            )
        }
        Log.d("PHOTO_TAKEN", "setPhotoTaken: has successfully taken image!")
    }
}