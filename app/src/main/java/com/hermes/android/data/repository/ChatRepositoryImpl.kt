package com.hermes.android.data.repository

import com.hermes.android.data.local.db.dao.ChatDao
import com.hermes.android.data.local.db.dao.SessionDao
import com.hermes.android.domain.model.Chat
import com.hermes.android.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val sessionDao: SessionDao
) : ChatRepository {

    override suspend fun create(chat: Chat): Long {
        return chatDao.insert(chat)
    }

    override suspend fun getById(id: Long): Chat? {
        return chatDao.getById(id)
    }

    override fun getBySessionId(sessionId: Long): Flow<List<Chat>> {
        return chatDao.getBySessionId(sessionId)
    }

    override suspend fun getBySessionIdPaged(sessionId: Long, limit: Int, offset: Int): List<Chat> {
        return chatDao.getBySessionIdPaged(sessionId, limit, offset)
    }

    override suspend fun getLastMessage(sessionId: Long): Chat? {
        return chatDao.getLastMessage(sessionId)
    }

    override suspend fun countBySessionId(sessionId: Long): Int {
        return chatDao.countBySessionId(sessionId)
    }

    override suspend fun update(chat: Chat) {
        chatDao.update(chat)
    }

    override suspend fun updateContent(id: Long, content: String, isStreaming: Boolean) {
        chatDao.updateContent(id, content, isStreaming, Clock.System.now().toString())
    }

    override suspend fun setStreaming(id: Long, isStreaming: Boolean) {
        chatDao.setStreaming(id, isStreaming)
    }

    override suspend fun setError(id: Long, error: String) {
        chatDao.setError(id, error)
    }

    override suspend fun deleteById(id: Long) {
        chatDao.deleteById(id)
    }

    override suspend fun deleteBySessionId(sessionId: Long) {
        chatDao.deleteBySessionId(sessionId)
    }
}