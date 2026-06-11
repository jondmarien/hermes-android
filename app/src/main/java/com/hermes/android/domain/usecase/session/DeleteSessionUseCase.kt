package com.hermes.android.domain.usecase.session

import com.hermes.android.domain.repository.ChatRepository
import com.hermes.android.domain.repository.MessageRepository
import com.hermes.android.domain.repository.SessionRepository
import javax.inject.Inject

class DeleteSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository
) {
    operator fun invoke(sessionId: Long) {
        // Delete messages first (cascade should handle this, but being explicit)
        chatRepository.deleteBySessionId(sessionId)
        // Then delete session
        sessionRepository.delete(sessionId)
    }
}