package com.hermes.android.data.repository

import com.hermes.android.data.local.db.dao.SessionDao
import com.hermes.android.domain.model.Session
import com.hermes.android.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {

    override suspend fun create(session: Session): Long {
        return sessionDao.insert(session)
    }

    override suspend fun getById(id: Long): Session? {
        return sessionDao.getById(id)
    }

    override fun getActiveSessions(): Flow<List<Session>> {
        return sessionDao.getActiveSessions()
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions()
    }

    override fun getSessionFlow(id: Long): Flow<Session?> {
        return sessionDao.getSessionFlow(id)
    }

    override suspend fun update(session: Session) {
        sessionDao.update(session.copy(updatedAt = Clock.System.now()))
    }

    override suspend fun updateTitle(id: Long, title: String) {
        sessionDao.updateTitle(id, title, Clock.System.now().toString())
    }

    override suspend fun setActive(id: Long, isActive: Boolean) {
        sessionDao.setActive(id, isActive, Clock.System.now().toString())
    }

    override suspend fun incrementMessageCount(id: Long, preview: String) {
        sessionDao.incrementMessageCount(id, preview, Clock.System.now().toString())
    }

    override suspend fun delete(id: Long) {
        sessionDao.deleteById(id)
    }

    override suspend fun deleteAll() {
        // This would need a raw query, for now delete active sessions
        sessionDao.getAllSessions().first().forEach { sessionDao.deleteById(it.id) }
    }
}