package com.hermes.android.domain.usecase.chat

import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.domain.repository.RemoteHermesRepository
import com.hermes.android.domain.repository.Result
import com.hermes.android.domain.repository.StreamChunk
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StreamMessageUseCase @Inject constructor(
    private val remoteHermesRepository: RemoteHermesRepository
) {
    operator fun invoke(
        config: RemoteConfig,
        messages: List<com.hermes.android.domain.repository.ChatMessage>,
        model: String? = null
    ): Flow<Result<StreamChunk>> {
        return remoteHermesRepository.streamMessage(
            baseUrl = config.baseUrl,
            apiKey = config.apiKey,
            messages = messages,
            model = model
        )
    }
}