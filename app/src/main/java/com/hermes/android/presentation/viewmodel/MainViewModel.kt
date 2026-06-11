package com.hermes.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.domain.model.ConnectionMode
import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.domain.model.Session
import com.hermes.android.domain.model.TermuxStatus
import com.hermes.android.domain.usecase.chat.GetMessagesUseCase
import com.hermes.android.domain.usecase.chat.SendMessageUseCase
import com.hermes.android.domain.usecase.remote.GetRemoteModelsUseCase
import com.hermes.android.domain.usecase.remote.GetRemoteCapabilitiesUseCase
import com.hermes.android.domain.usecase.remote.TestRemoteConnectionUseCase
import com.hermes.android.domain.usecase.session.CreateSessionUseCase
import com.hermes.android.domain.usecase.session.DeleteSessionUseCase
import com.hermes.android.domain.usecase.session.GetSessionUseCase
import com.hermes.android.domain.usecase.session.GetSessionsUseCase
import com.hermes.android.domain.usecase.session.RenameSessionUseCase
import com.hermes.android.domain.usecase.termux.GetTermuxStatusUseCase
import com.hermes.android.domain.usecase.termux.InstallHermesInTermuxUseCase
import com.hermes.android.domain.usecase.termux.StartHermesLocalUseCase
import com.hermes.android.domain.usecase.termux.StopHermesLocalUseCase
import com.hermes.android.data.local.preferences.HermesPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferences: HermesPreferences,
    private val getSessionsUseCase: GetSessionsUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val renameSessionUseCase: RenameSessionUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val testRemoteConnectionUseCase: TestRemoteConnectionUseCase,
    private val getRemoteModelsUseCase: GetRemoteModelsUseCase,
    private val getRemoteCapabilitiesUseCase: GetRemoteCapabilitiesUseCase,
    private val getTermuxStatusUseCase: GetTermuxStatusUseCase,
    private val startHermesLocalUseCase: StartHermesLocalUseCase,
    private val stopHermesLocalUseCase: StopHermesLocalUseCase,
    private val installHermesInTermuxUseCase: InstallHermesInTermuxUseCase
) : ViewModel() {

    // Navigation state
    enum class Destination {
        CHAT, SESSIONS, SETTINGS, TERMUX_SETUP, LOG_VIEWER, REMOTE_CONFIG_DIALOG
    }

    private val _currentDestination = MutableStateFlow(Destination.CHAT)
    val currentDestination = _currentDestination.asStateFlow()

    private val _activeSessionId = MutableStateFlow<Long?>(null)
    val activeSessionId = _activeSessionId.asStateFlow()

    // Sessions
    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions = _sessions.asStateFlow()

    // Current session
    private val _currentSession = MutableStateFlow<Session?>(null)
    val currentSession = _currentSession.asStateFlow()

    // Messages for active session
    private val _messages = MutableStateFlow<List<com.hermes.android.domain.model.Chat>>(emptyList())
    val messages = _messages.asStateFlow()

    // Connection mode
    private val _connectionMode = MutableStateFlow(preferences.connectionMode.getValue())
    val connectionMode = _connectionMode.asStateFlow()

    // Remote configs
    private val _remoteConfigs = MutableStateFlow<List<RemoteConfig>>(emptyList())
    val remoteConfigs = _remoteConfigs.asStateFlow()

    private val _activeRemoteConfig = MutableStateFlow<RemoteConfig?>(null)
    val activeRemoteConfig = _activeRemoteConfig.asStateFlow()

    // Termux status
    private val _termuxStatus = MutableStateFlow<TermuxStatus?>(null)
    val termuxStatus = _termuxStatus.asStateFlow()

    // Streaming state
    private val _isStreaming = MutableStateFlow(false)
    val isStreaming = _isStreaming.asStateFlow()

    // Loading states
    private val _isLoadingSessions = MutableStateFlow(false)
    val isLoadingSessions = _isLoadingSessions.asStateFlow()

    private val _isSendingMessage = MutableStateFlow(false)
    val isSendingMessage = _isSendingMessage.asStateFlow()

    // Dialog states
    private val _showRemoteConfigDialog = MutableStateFlow<RemoteConfig?>(null)
    val showRemoteConfigDialog = _showRemoteConfigDialog.asStateFlow()

    init {
        loadSessions()
        loadRemoteConfigs()
        observeTermuxStatus()
    }

    private fun observeTermuxStatus() {
        viewModelScope.launch {
            getTermuxStatusUseCase().let { status ->
                _termuxStatus.value = status
            }
        }
    }

    fun loadSessions() {
        _isLoadingSessions.value = true
        viewModelScope.launch {
            getSessionsUseCase().collect { sessionList ->
                _sessions.value = sessionList
                _isLoadingSessions.value = false

                // Set active session if none selected
                if (_activeSessionId.value == null && sessionList.isNotEmpty()) {
                    val firstActive = sessionList.firstOrNull { it.isActive }
                    _activeSessionId.value = firstActive?.id ?: sessionList.first().id
                }
            }
        }
    }

    fun onSessionSelected(sessionId: Long) {
        _activeSessionId.value = sessionId
        _currentDestination.value = Destination.CHAT
        loadMessages(sessionId)
        loadSessionDetails(sessionId)
    }

    private fun loadSessionDetails(sessionId: Long) {
        viewModelScope.launch {
            getSessionUseCase().getById(sessionId).let { session ->
                session?.let {
                    _currentSession.value = it
                    _connectionMode.value = it.mode
                    if (it.mode == ConnectionMode.REMOTE && it.remoteConfigId != null) {
                        loadActiveRemoteConfig(it.remoteConfigId!!)
                    }
                }
            }
        }
    }

    private fun loadActiveRemoteConfig(configId: Long) {
        viewModelScope.launch {
            _remoteConfigs.value.firstOrNull { it.id == configId }?.let {
                _activeRemoteConfig.value = it
            }
        }
    }

    private fun loadMessages(sessionId: Long) {
        viewModelScope.launch {
            getMessagesUseCase().invoke(sessionId).collect { msgList ->
                _messages.value = msgList
            }
        }
    }

    fun loadRemoteConfigs() {
        viewModelScope.launch {
            // Load from preferences or repository
            preferences.remoteConfigsJsonFlow.collect { json ->
                // Parse JSON to RemoteConfig list
                // For now, use empty list
                _remoteConfigs.value = emptyList()
            }
        }
    }

    fun createNewSession(title: String? = null) {
        val mode = _connectionMode.value
        val configId = _activeRemoteConfig.value?.id
        val session = createSessionUseCase(title, mode, configId)
        _activeSessionId.value = session.id
        _currentDestination.value = Destination.CHAT
        loadSessions()
    }

    fun deleteSession(sessionId: Long) {
        deleteSessionUseCase(sessionId)
        if (_activeSessionId.value == sessionId) {
            _activeSessionId.value = null
        }
        loadSessions()
    }

    fun renameSession(sessionId: Long, newTitle: String) {
        renameSessionUseCase(sessionId, newTitle)
        loadSessions()
    }

    fun sendMessage(content: String, model: String? = null) {
        _isSendingMessage.value = true
        _isStreaming.value = true
        _activeSessionId.value?.let { sessionId ->
            val mode = _connectionMode.value
            val remoteConfig = _activeRemoteConfig.value
            viewModelScope.launch {
                sendMessageUseCase(sessionId, content, mode, remoteConfig, model).collect { result ->
                    // Result handling - messages are added by the use case
                    _isStreaming.value = false
                    _isSendingMessage.value = false
                }
            }
        }
    }

    fun testRemoteConnection(config: RemoteConfig) {
        viewModelScope.launch {
            testRemoteConnectionUseCase(config)
        }
    }

    fun startHermes() {
        viewModelScope.launch {
            val status = startHermesLocalUseCase()
            _termuxStatus.value = status
        }
    }

    fun stopHermes() {
        viewModelScope.launch {
            val status = stopHermesLocalUseCase()
            _termuxStatus.value = status
        }
    }

    fun installHermes() {
        viewModelScope.launch {
            installHermesInTermuxUseCase().onSuccess {
                loadTermuxStatus()
            }
        }
    }

    fun loadTermuxStatus() {
        viewModelScope.launch {
            val status = getTermuxStatusUseCase()
            _termuxStatus.value = status
        }
    }

    fun setConnectionMode(mode: ConnectionMode) {
        _connectionMode.value = mode
        preferences.setConnectionMode(mode.name)
    }

    fun setAutoStartLocal(enabled: Boolean) {
        preferences.setAutoStartLocal(enabled)
    }

    fun setTermuxPath(path: String) {
        preferences.setTermuxPath(path)
    }

    fun showRemoteConfigDialog(config: RemoteConfig? = null) {
        _showRemoteConfigDialog.value = config
        _currentDestination.value = Destination.REMOTE_CONFIG_DIALOG
    }

    fun dismissRemoteConfigDialog() {
        _showRemoteConfigDialog.value = null
        _currentDestination.value = Destination.SETTINGS
    }

    fun showSessions() {
        _currentDestination.value = Destination.SESSIONS
    }

    fun showSettings() {
        _currentDestination.value = Destination.SETTINGS
    }

    fun showTermuxSetup() {
        _currentDestination.value = Destination.TERMUX_SETUP
    }

    fun showLogViewer() {
        _currentDestination.value = Destination.LOG_VIEWER
    }

    fun goBack() {
        _currentDestination.value = Destination.CHAT
    }
}