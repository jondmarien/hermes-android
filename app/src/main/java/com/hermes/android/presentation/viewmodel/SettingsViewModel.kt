package com.hermes.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.data.local.preferences.HermesPreferences
import com.hermes.android.domain.model.ConnectionMode
import com.hermes.android.domain.model.RemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: HermesPreferences
) : ViewModel() {

    // Connection mode
    val connectionMode = preferences.connectionModeFlow
        .map { ConnectionMode.valueOf(it) }
        .distinctUntilChanged()
        .stateIn(viewModelScope, androidx.lifecycle.viewModelScope.coroutineContext, ConnectionMode.REMOTE)

    fun setConnectionMode(mode: ConnectionMode) {
        viewModelScope.launch {
            preferences.setConnectionMode(mode.name)
        }
    }

    // Remote URL
    val remoteUrl = preferences.remoteUrlFlow
        .distinctUntilChanged()
        .stateIn(viewModelScope, androidx.lifecycle.viewModelScope.coroutineContext, "http://10.0.2.2:8642/v1")

    fun setRemoteUrl(url: String) {
        viewModelScope.launch {
            preferences.setRemoteUrl(url)
        }
    }

    // Remote API Key
    val remoteApiKey = preferences.remoteApiKeyFlow
        .distinctUntilChanged()
        .stateIn(viewModelScope, androidx.lifecycle.viewModelScope.coroutineContext, "")

    fun setRemoteApiKey(key: String) {
        viewModelScope.launch {
            preferences.setRemoteApiKey(key)
        }
    }

    // Termux path
    val termuxPath = preferences.termuxPathFlow
        .distinctUntilChanged()
        .stateIn(viewModelScope, androidx.lifecycle.viewModelScope.coroutineContext, "/data/data/com.termux/files/usr/bin/bash")

    fun setTermuxPath(path: String) {
        viewModelScope.launch {
            preferences.setTermuxPath(path)
        }
    }

    // Auto-start local
    val autoStartLocal = preferences.autoStartLocalFlow
        .distinctUntilChanged()
        .stateIn(viewModelScope, androidx.lifecycle.viewModelScope.coroutineContext, false)

    fun setAutoStartLocal(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setAutoStartLocal(enabled)
        }
    }

    // Notifications
    val notificationsEnabled = preferences.notificationsEnabledFlow
        .distinctUntilChanged()
        .stateIn(viewModelScope, androidx.lifecycle.viewModelScope.coroutineContext, true)

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setNotificationsEnabled(enabled)
        }
    }

    // Theme
    val theme = preferences.themeFlow
        .distinctUntilChanged()
        .stateIn(viewModelScope, androidx.lifecycle.viewModelScope.coroutineContext, "SYSTEM")

    fun setTheme(theme: String) {
        viewModelScope.launch {
            preferences.setTheme(theme)
        }
    }

    // Language
    val language = preferences.languageFlow
        .distinctUntilChanged()
        .stateIn(viewModelScope, androidx.lifecycle.viewModelScope.coroutineContext, "SYSTEM")

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            preferences.setLanguage(lang)
        }
    }

    // Remote configs (stored as JSON in preferences)
    val remoteConfigsJson = preferences.remoteConfigsJsonFlow
        .distinctUntilChanged()
        .stateIn(viewModelScope, androidx.lifecycle.viewModelScope.coroutineContext, "[]")

    fun getRemoteConfigs(): List<RemoteConfig> {
        // Parse JSON - would use kotlinx.serialization
        return emptyList()
    }

    fun addRemoteConfig(config: RemoteConfig) {
        viewModelScope.launch {
            val current = getRemoteConfigs()
            val updated = current + config
            saveRemoteConfigs(updated)
        }
    }

    fun updateRemoteConfig(config: RemoteConfig) {
        viewModelScope.launch {
            val current = getRemoteConfigs()
            val updated = current.map { if (it.id == config.id) config else it }
            saveRemoteConfigs(updated)
        }
    }

    fun deleteRemoteConfig(id: Long) {
        viewModelScope.launch {
            val current = getRemoteConfigs()
            val updated = current.filter { it.id != id }
            saveRemoteConfigs(updated)
        }
    }

    fun setDefaultRemoteConfig(id: Long) {
        viewModelScope.launch {
            val current = getRemoteConfigs()
            val updated = current.map { 
                it.copy(isDefault = it.id == id)
            }
            saveRemoteConfigs(updated)
        }
    }

    private fun saveRemoteConfigs(configs: List<RemoteConfig>) {
        // Serialize to JSON and save
        // preferences.setRemoteConfigsJson(jsonString)
    }
}