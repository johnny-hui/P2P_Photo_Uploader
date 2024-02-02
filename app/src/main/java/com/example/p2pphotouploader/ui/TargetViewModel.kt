package com.example.p2pphotouploader.ui

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.p2pphotouploader.data.TargetUiState
import com.example.p2pphotouploader.utility.FAILURE
import com.example.p2pphotouploader.utility.SUCCESS
import com.example.p2pphotouploader.utility.uploadPhoto
import com.example.p2pphotouploader.utility.validateIP
import com.example.p2pphotouploader.utility.validatePort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TargetViewModel : ViewModel() {
     //Exposes UI state to UI components and viewModel
    private val _uiState = MutableStateFlow(TargetUiState())
    val uiState: StateFlow<TargetUiState> = _uiState.asStateFlow()

    // Setters (to update UI state)
    fun setPhotoTaken(bitmap: Bitmap) {
        _uiState.update { currentState ->
            currentState.copy(
                capturedImage = bitmap
            )
        }
    }

    fun checkIPAddress() {
        if(targetIP.isEmpty()) {
            targetIP = "Cannot be empty!"
            _uiState.update { currentState ->
                currentState.copy(
                    ipAddress = "",
                    isIncorrectIPFormat = true,
                    isIPValid = false,
                )
            }
        } else if(validateIP(targetIP)) {
            _uiState.update { currentState ->
                currentState.copy(
                    ipAddress = targetIP,
                    isIncorrectIPFormat = false,
                    showUploadError = false,
                    isIPValid = true,
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    ipAddress = "",
                    isIncorrectIPFormat = true,
                    isIPValid = false
                )
            }
        }
    }

    fun checkPortNumber() {
        try {
            val portToInt = targetPort.toInt()

            if(validatePort(portToInt)) {
                _uiState.update { currentState ->
                    currentState.copy(
                        targetPort = portToInt,
                        isIncorrectPortFormat = false,
                        showUploadError = false,
                        isPortValid = true
                    )
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(
                        targetPort = 0,
                        isIncorrectPortFormat = true,
                        isPortValid = false
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { currentState ->
                currentState.copy(
                    targetPort = 0,
                    isIncorrectPortFormat = true,
                    isPortValid = false
                )
            }
        }
    }

    /**
     * Performs and handles the upload photo functionality
     */
    fun uploadPhotoHandler() : String {
        // a) Validate Fields first (Use AlertDialog to display error)
        if(uiState.value.isIncorrectIPFormat
                or uiState.value.isIncorrectPortFormat
                or targetIP.isEmpty()
                or targetPort.isEmpty()
        ) {
            _uiState.update { currentState ->
                currentState.copy(
                    showTransferErrorMsg = false,
                    showUploadError = true
                )
            }
            return FAILURE
        }
        // b) Perform Transfer (if input valid)
        else {
            _uiState.update { currentState ->
                currentState.copy(
                    isTransferring = true
                )
            }

            if(uploadPhoto(targetIP, uiState.value.targetPort, uiState.value.capturedImage) == SUCCESS) {
                resetState()
                return SUCCESS
            } else {
                _uiState.update { currentState ->
                    currentState.copy(
                        isTransferring = false,
                        showUploadError = true,
                        showTransferErrorMsg = true
                    )
                }
                return FAILURE
            }
        }
    }

    // For TextField States (ConfigurationScreen)
    var targetIP by mutableStateOf("")
    var targetPort by mutableStateOf("")

    fun updateIPAddress(inputIP: String) {
        targetIP = inputIP
    }

    fun updatePort(inputPort: String) {
        targetPort = inputPort
    }

    /**
     * Resets the error states on dialog close (ConfigurationScreen)
     */
    fun onCloseConfigDialogError() {
        _uiState.update { currentState ->
            currentState.copy(
                showUploadError = !currentState.showUploadError,
            )
        }
    }

    private fun resetState() {
        targetIP = ""
        targetPort = ""
        _uiState.value = TargetUiState()
    }
}
