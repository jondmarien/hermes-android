package com.hermes.android.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesKeys
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.rxpreferences.RxPreferenceDataStoreBuilder
import androidx.datastore.preferences.rxpreferences.preferencesDataStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromString
import kotlinx.serialization.json.encodeToString

class HermesPreferences @Inject constructor(
    private val context: Context
) {

    companion object {
        private const stringPreferencesKey(name: String) = PreferencesKeys.stringKey(name)
        private const booleanPreferencesKey(name: String) = PreferencesKeys.booleanKey(name)
        private const longPreferencesKey(name: String) = PreferencesKeys.longKey(name)
    }

    // Keys
    private val KEY_CONNECTION_MODE = stringPreferencesKey("connection_mode")
    private val KEY_REMOTE_URL = stringPreferencesKey("remote_url")
    private val KEY_REMOTE_API_KEY = stringPreferencesKey("remote_api_key")
    private val KEY_REMOTE_CONFIG_ID = longPreferencesKey("remote_config_id")
    private val KEY_TERMUX_PATH = stringPreferencesKey("termux_path")
    private val KEY_AUTO_START_LOCAL = booleanPreferencesKey("auto_start_local")
    private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    private val KEY_THEME = stringPreferencesKey("theme")
    private val KEY_LANGUAGE = stringPreferencesKey("language")
    private val KEY_REMOTE_CONFIGS_JSON = stringPreferencesKey("remote_configs_json")

    private val dataStore = preferencesDataStore(
        RxPreferenceDataStoreBuilder(context, "hermes_preferences")
    )

    // Connection Mode
    suspend fun getConnectionMode(): String {
        return dataStore.data
            .map { it[KEY_CONNECTION_MODE] ?: "REMOTE" }
            .first()
    }

    suspend fun setConnectionMode(mode: String) {
        dataStore.edit { it[KEY_CONNECTION_MODE] = mode }
    }

    val connectionModeFlow: Flow<String> = dataStore.data
        .map { it[KEY_CONNECTION_MODE] ?: "REMOTE" }
        .distinctUntilChanged()

    // Remote URL
    suspend fun getRemoteUrl(): String {
        return dataStore.data
            .map { it[KEY_REMOTE_URL] ?: "http://10.0.2.2:8642/v1" }
            .first()
    }

    suspend fun setRemoteUrl(url: String) {
        dataStore.edit { it[KEY_REMOTE_URL] = url }
    }

    val remoteUrlFlow: Flow<String> = dataStore.data
        .map { it[KEY_REMOTE_URL] ?: "http://10.0.2.2:8642/v1" }
        .distinctUntilChanged()

    // Remote API Key
    suspend fun getRemoteApiKey(): String {
        return dataStore.data
            .map { it[KEY_REMOTE_API_KEY] ?: "" }
            .first()
    }

    suspend fun setRemoteApiKey(apiKey: String) {
        dataStore.edit { it[KEY_REMOTE_API_KEY] = apiKey }
    }

    val remoteApiKeyFlow: Flow<String> = dataStore.data
        .map { it[KEY_REMOTE_API_KEY] ?: "" }
        .distinctUntilChanged()

    // Remote Config ID
    suspend fun getRemoteConfigId(): Long {
        return dataStore.data
            .map { it[KEY_REMOTE_CONFIG_ID] ?: 0L }
            .first()
    }

    suspend fun setRemoteConfigId(id: Long) {
        dataStore.edit { it[KEY_REMOTE_CONFIG_ID] = id }
    }

    val remoteConfigIdFlow: Flow<Long> = dataStore.data
        .map { it[KEY_REMOTE_CONFIG_ID] ?: 0L }
        .distinctUntilChanged()

    // Termux Path
    suspend fun getTermuxPath(): String {
        return dataStore.data
            .map { it[KEY_TERMUX_PATH] ?: "/data/data/com.termux/files/usr/bin" }
            .first()
    }

    suspend fun setTermuxPath(path: String) {
        dataStore.edit { it[KEY_TERMUX_PATH] = path }
    }

    val termuxPathFlow: Flow<String> = dataStore.data
        .map { it[KEY_TERMUX_PATH] ?: "/data/data/com.termux/files/usr/bin" }
        .distinctUntilChanged()

    // Auto Start Local
    suspend fun getAutoStartLocal(): Boolean {
        return dataStore.data
            .map { it[KEY_AUTO_START_LOCAL] ?: false }
            .first()
    }

    suspend fun setAutoStartLocal(enabled: Boolean) {
        dataStore.edit { it[KEY_AUTO_START_LOCAL] = enabled }
    }

    val autoStartLocalFlow: Flow<Boolean> = dataStore.data
        .map { it[KEY_AUTO_START_LOCAL] ?: false }
        .distinctUntilChanged()

    // Notifications
    suspend fun getNotificationsEnabled(): Boolean {
        return dataStore.data
            .map { it[KEY_NOTIFICATIONS_ENABLED] ?: true }
            .first()
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_NOTIFICATIONS_ENABLED] = enabled }
    }

    val notificationsEnabledFlow: Flow<Boolean> = dataStore.data
        .map { it[KEY_NOTIFICATIONS_ENABLED] ?: true }
        .distinctUntilChanged()

    // Theme
    suspend fun getTheme(): String {
        return dataStore.data
            .map { it[KEY_THEME] ?: "SYSTEM" }
            .first()
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit { it[KEY_THEME] = theme }
    }

    val themeFlow: Flow<String> = dataStore.data
        .map { it[KEY_THEME] ?: "SYSTEM" }
        .distinctUntilChanged()

    // Language
    suspend fun getLanguage(): String {
        return dataStore.data
            .map { it[KEY_LANGUAGE] ?: "SYSTEM" }
            .first()
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { it[KEY_LANGUAGE] = language }
    }

    val languageFlow: Flow<String> = dataStore.data
        .map { it[KEY_LANGUAGE] ?: "SYSTEM" }
        .distinctUntilChanged()

    // Remote Configs (stored as JSON)
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun getRemoteConfigsJson(): String {
        return dataStore.data
            .map { it[KEY_REMOTE_CONFIGS_JSON] ?: "[]" }
            .first()
    }

    suspend fun setRemoteConfigsJson(jsonString: String) {
        dataStore.edit { it[KEY_REMOTE_CONFIGS_JSON] = jsonString }
    }

    val remoteConfigsJsonFlow: Flow<String> = dataStore.data
        .map { it[KEY_REMOTE_CONFIGS_JSON] ?: "[]" }
        .distinctUntilChanged()
}