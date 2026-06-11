package com.hermes.android.domain.usecase.session

import com.hermes.android.domain.model.Session
import com.hermes.android.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(sessionId: Long): Flow<Session?> {
        return sessionRepository.getSessionFlow(sessionId)
    }

    suspend fun getById(sessionId: Long): Session? {
        return sessionRepository.getById(sessionId)
    }
}