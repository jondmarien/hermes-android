package com.hermes.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.domain.usecase.session.GetSessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSessionsUseCase: GetSessionsUseCase
) : ViewModel() {

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            getSessionsUseCase()
        }
    }
}