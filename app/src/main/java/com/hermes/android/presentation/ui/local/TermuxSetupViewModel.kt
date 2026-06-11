package com.hermes.android.presentation.ui.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.domain.model.TermuxStatus
import com.hermes.android.domain.usecase.termux.GetTermuxStatusUseCase
import com.hermes.android.domain.usecase.termux.InstallHermesInTermuxUseCase
import com.hermes.android.domain.usecase.termux.StartHermesLocalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermuxSetupViewModel @Inject constructor(
    private val getTermuxStatusUseCase: GetTermuxStatusUseCase,
    private val installHermesInTermuxUseCase: InstallHermesInTermuxUseCase,
    private val startHermesLocalUseCase: StartHermesLocalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TermuxSetupUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkStatus()
    }

    fun checkStatus() {
        viewModelScope.launch {
            val status = getTermuxStatusUseCase()
            _uiState.updateIt { it.copy(
                isTermuxInstalled = status.isInstalled,
                isHermesInstalled = status.hermesInstalled,
                statusMessage = if (!status.isInstalled) "Termux not installed"
                else if (!status.hermesInstalled) "Hermes not installed in Termux"
                else "Ready to start Hermes"
            ) }
        }
    }

    fun installHermes() {
        _uiState.updateIt { it.copy(isInstalling = true, statusMessage = "Installing Hermes in Termux…") }
        viewModelScope.launch {
            val result = installHermesInTermuxUseCase()
            result.onSuccess {
                checkStatus()
                _uiState.updateIt { it.copy(isInstalling = false, statusMessage = "Hermes installed successfully") }
            }.onFailure { e ->
                _uiState.updateIt { it.copy(isInstalling = false, statusMessage = "Install failed: ${e.message}") }
            }
        }
    }

    fun startHermes() {
        _uiState.updateIt { it.copy(isStarting = true, statusMessage = "Starting Hermes Gateway…") }
        viewModelScope.launch {
            val status = startHermesLocalUseCase()
            checkStatus()
            _uiState.updateIt { it.copy(isStarting = false) }
        }
    }

    fun openTermuxFdroid() {
        // This would open F-Droid, handled in the UI layer
    }
}

data class TermuxSetupUiState(
    val isTermuxInstalled: Boolean = false,
    val isHermesInstalled: Boolean = false,
    val isInstalling: Boolean = false,
    val isStarting: Boolean = false,
    val statusMessage: String = "Checking Termux…"
)