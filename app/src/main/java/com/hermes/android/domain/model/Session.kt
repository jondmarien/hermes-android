package com.hermes.android.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Entity(tableName = "sessions")
@Serializable
data class Session(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val mode: ConnectionMode = ConnectionMode.REMOTE,
    val remoteConfigId: Long? = null,
    val isActive: Boolean = true,
    val messageCount: Int = 0,
    val lastMessagePreview: String? = null
) {
    companion object {
        fun create(title: String, mode: ConnectionMode = ConnectionMode.REMOTE, remoteConfigId: Long? = null): Session {
            val now = Instant.now()
            return Session(
                title = title,
                createdAt = now,
                updatedAt = now,
                mode = mode,
                remoteConfigId = remoteConfigId,
                isActive = true,
                messageCount = 0
            )
        }
    }
}

enum class ConnectionMode {
    LOCAL,
    REMOTE
}

@Serializable
data class RemoteConfig(
    val id: Long = 0,
    val name: String,
    val baseUrl: String,
    val apiKey: String,
    val isDefault: Boolean = false,
    val createdAt: Instant = Instant.now()
)

@Serializable
data class TermuxStatus(
    val isInstalled: Boolean,
    val isRunning: Boolean,
    val hermesInstalled: Boolean,
    val version: String? = null,
    val lastOutput: String? = null,
    val error: String? = null
)