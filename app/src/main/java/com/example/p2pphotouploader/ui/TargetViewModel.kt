package com.example.p2pphotouploader.ui

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.p2pphotouploader.data.EMPTY_TEXTFIELD_MSG
import com.example.p2pphotouploader.data.TargetUiState
import com.example.p2pphotouploader.utility.FAILURE
import com.example.p2pphotouploader.utility.SUCCESS
import com.example.p2pphotouploader.utility.uploadPhotoAsync
import com.example.p2pphotouploader.utility.validateIP
import com.example.p2pphotouploader.utility.validatePort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class TargetViewModel : ViewModel() {
     //Exposes UI state to UI components and viewModel
    private val _uiState = MutableStateFlow(TargetUiState())
    val uiState: StateFlow<TargetUiState> = _uiState.asStateFlow()

    // Public States
    var targetIP by mutableStateOf("")
    var targetPort by mutableStateOf("")

    /**
     * Sets the capturedImage property of the UI State
     */
    fun setPhotoTaken(bitmap: Bitmap) {
        _uiState.update { currentState ->
            currentState.copy(
                capturedImage = bitmap
            )
        }
    }

    /**
     * Verifies the IP address (entered in textField) and
     * updates the property in the UI state
     */
    fun checkIPAddress() {
        if(targetIP.isEmpty()) {
            targetIP = EMPTY_TEXTFIELD_MSG
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

    /**
     * Verifies the port number (entered in textField) and
     * updates the property in the UI state
     */
    fun checkPortNumber() {
        try {
            if(targetPort.isEmpty()) {
                targetPort = EMPTY_TEXTFIELD_MSG
            }

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
    suspend fun uploadPhotoHandler() : String {
        var result = ""

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
        // b) Perform Transfer (if inputs are valid)
        else {
            _uiState.update { currentState ->
                currentState.copy(
                    isTransferring = true
                )
            }

            // Use Co-routines here
            withContext(Dispatchers.IO) {
                if(uploadPhotoAsync(targetIP, uiState.value.targetPort, uiState.value.capturedImage) == SUCCESS) {
                    resetState()
                    result = SUCCESS
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isTransferring = false,
                            showUploadError = true,
                            showTransferErrorMsg = true
                        )
                    }
                    result = FAILURE
                }
            }
            return result
        }
    }


    /**
     * Updates the IP address TextField state
     */
    fun updateIPAddress(inputIP: String) {
        targetIP = inputIP
    }

    /**
     * Updates the port number TextField state
     */
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
