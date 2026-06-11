package com.hermes.android.domain.repository

import com.hermes.android.domain.model.Chat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun create(chat: Chat): Long
    suspend fun getById(id: Long): Chat?
    fun getBySessionId(sessionId: Long): Flow<List<Chat>>
    suspend fun getBySessionIdPaged(sessionId: Long, limit: Int, offset: Int): List<Chat>
    suspend fun getLastMessage(sessionId: Long): Chat?
    suspend fun countBySessionId(sessionId: Long): Int
    suspend fun update(chat: Chat)
    suspend fun updateContent(id: Long, content: String, isStreaming: Boolean)
    suspend fun setStreaming(id: Long, isStreaming: Boolean)
    suspend fun setError(id: Long, error: String)
    suspend fun deleteById(id: Long)
    suspend fun deleteBySessionId(sessionId: Long)
}