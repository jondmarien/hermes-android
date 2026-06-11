package com.hermes.android.domain.usecase.remote

import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.domain.repository.RemoteHermesRepository
import com.hermes.android.domain.repository.Result
import com.hermes.android.domain.repository.Capabilities
import javax.inject.Inject

class GetRemoteCapabilitiesUseCase @Inject constructor(
    private val remoteHermesRepository: RemoteHermesRepository
) {
    operator fun invoke(config: RemoteConfig): Result<Capabilities> {
        return remoteHermesRepository.getCapabilities(config.baseUrl, config.apiKey)
    }
}