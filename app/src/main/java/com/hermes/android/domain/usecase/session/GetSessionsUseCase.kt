package com.hermes.android.domain.usecase.session

import com.hermes.android.domain.model.Session
import com.hermes.android.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<List<Session>> {
        return sessionRepository.getActiveSessions()
    }

    fun getAll(): Flow<List<Session>> {
        return sessionRepository.getAllSessions()
    }
}