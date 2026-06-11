package com.hermes.android.domain.usecase.session

import com.hermes.android.domain.model.ConnectionMode
import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.domain.model.Session
import com.hermes.android.domain.repository.RemoteConfigRepository
import com.hermes.android.domain.repository.SessionRepository
import kotlinx.datetime.Clock
import javax.inject.Inject

class CreateSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val remoteConfigRepository: RemoteConfigRepository
) {
    operator fun invoke(
        title: String? = null,
        mode: ConnectionMode = ConnectionMode.REMOTE,
        remoteConfigId: Long? = null
    ): Session {
        val configId = remoteConfigId ?: remoteConfigRepository.getDefault()?.id
        val sessionTitle = title ?: "New Chat ${Clock.System.now().year}-${Clock.System.now().month.number}-${Clock.System.now().dayOfMonth}"
        val session = Session.create(sessionTitle, mode, configId)
        val id = sessionRepository.create(session)
        return session.copy(id = id)
    }
}