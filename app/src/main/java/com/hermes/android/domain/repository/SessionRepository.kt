package com.hermes.android.domain.repository

import com.hermes.android.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun create(session: Session): Long
    suspend fun getById(id: Long): Session?
    fun getActiveSessions(): Flow<List<Session>>
    fun getAllSessions(): Flow<List<Session>>
    fun getSessionFlow(id: Long): Flow<Session?>
    suspend fun update(session: Session)
    suspend fun updateTitle(id: Long, title: String)
    suspend fun setActive(id: Long, isActive: Boolean)
    suspend fun incrementMessageCount(id: Long, preview: String)
    suspend fun delete(id: Long)
    suspend fun deleteAll()
}