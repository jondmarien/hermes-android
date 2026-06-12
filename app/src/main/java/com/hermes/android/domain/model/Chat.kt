package com.hermes.android.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Entity(
    tableName = "chats",
    foreignKeys = [
        ForeignKey(
            entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
@Serializable
data class Chat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val role: MessageRole,
    val content: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val model: String? = null,
    val finishReason: String? = null,
    val toolCalls: String? = null, // JSON string
    val isStreaming: Boolean = false,
    val error: String? = null
) {
    companion object {
        fun createUserMessage(sessionId: Long, content: String): Chat {
            return Chat(sessionId = sessionId, role = MessageRole.USER, content = content)
        }

        fun createAssistantMessage(sessionId: Long, content: String, model: String? = null): Chat {
            return Chat(
                sessionId = sessionId,
                role = MessageRole.ASSISTANT,
                content = content,
                model = model
            )
        }

        fun createSystemMessage(sessionId: Long, content: String): Chat {
            return Chat(sessionId = sessionId, role = MessageRole.SYSTEM, content = content)
        }

        fun createToolMessage(sessionId: Long, content: String, toolCalls: String): Chat {
            return Chat(
                sessionId = sessionId,
                role = MessageRole.TOOL,
                content = content,
                toolCalls = toolCalls
            )
        }
    }
}

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM,
    TOOL
}