package com.hermes.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.data.local.preferences.HermesPreferences
import com.hermes.android.domain.model.ConnectionMode
import com.hermes.android.domain.model.TermuxStatus
import com.hermes.android.domain.usecase.termux.GetTermuxStatusUseCase
import com.hermes.android.domain.usecase.termux.InstallHermesInTermuxUseCase
import com.hermes.android.domain.usecase.termux.StartHermesLocalUseCase
import com.hermes.android.domain.usecase.termux.StopHermesLocalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalModeViewModel @Inject constructor(
    private val preferences: HermesPreferences,
    private val getTermuxStatusUseCase: GetTermuxStatusUseCase,
    private val startHermesLocalUseCase: StartHermesLocalUseCase,
    private val stopHermesLocalUseCase: StopHermesLocalUseCase,
    private val installHermesInTermuxUseCase: InstallHermesInTermuxUseCase
) : ViewModel() {

    private val _termuxStatus = MutableStateFlow<TermuxStatus?>(null)
    val termuxStatus = _termuxStatus.asStateFlow()

    private val _isAutoStartEnabled = MutableStateFlow(preferences.autoStartLocal)
    val isAutoStartEnabled = _isAutoStartEnabled.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadStatus()
        if (preferences.autoStartLocal && preferences.connectionMode == ConnectionMode.LOCAL) {
            startHermes()
        }
    }

    fun loadStatus() {
        _isLoading.value = true
        viewModelScope.launch {
            val status = getTermuxStatusUseCase()
            _termuxStatus.value = status
            _isLoading.value = false
        }
    }

    fun startHermes() {
        _isLoading.value = true
        viewModelScope.launch {
            val status = startHermesLocalUseCase()
            _termuxStatus.value = status
            _isLoading.value = false
        }
    }

    fun stopHermes() {
        _isLoading.value = true
        viewModelScope.launch {
            val status = stopHermesLocalUseCase()
            _termuxStatus.value = status
            _isLoading.value = false
        }
    }

    fun installHermes() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = installHermesInTermuxUseCase()
            result.onSuccess {
                loadStatus()
            }.onFailure { _ ->
                _isLoading.value = false
            }
        }
    }

    fun setAutoStart(enabled: Boolean) {
        _isAutoStartEnabled.value = enabled
        preferences.autoStartLocal = enabled
    }
}