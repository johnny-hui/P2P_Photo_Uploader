package com.example.p2pphotouploader.ui.theme

import androidx.lifecycle.ViewModel
import com.example.p2pphotouploader.data.TargetUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TargetViewModel : ViewModel() {
    /**
     * Exposes UI state to UI components and viewModel
     */
    private val _uiState = MutableStateFlow(TargetUiState())
    val uiState: StateFlow<TargetUiState> = _uiState.asStateFlow()



}