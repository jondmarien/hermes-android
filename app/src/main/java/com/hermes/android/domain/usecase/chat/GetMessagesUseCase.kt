package com.hermes.android.domain.usecase.chat

import com.hermes.android.data.local.db.dao.ChatDao
import com.hermes.android.domain.model.Chat
import com.hermes.android.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(sessionId: Long): Flow<List<Chat>> {
        return chatRepository.getBySessionId(sessionId)
    }

    fun getPaged(sessionId: Long, limit: Int, offset: Int): List<Chat> {
        return chatRepository.getBySessionIdPaged(sessionId, limit, offset)
    }
}