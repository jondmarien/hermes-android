package com.hermes.android.domain.usecase.session

import com.hermes.android.domain.repository.SessionRepository
import javax.inject.Inject

class RenameSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(sessionId: Long, newTitle: String) {
        sessionRepository.updateTitle(sessionId, newTitle)
    }
}