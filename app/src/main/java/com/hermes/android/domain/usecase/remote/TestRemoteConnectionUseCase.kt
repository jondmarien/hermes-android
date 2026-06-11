package com.hermes.android.domain.usecase.remote

import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.domain.repository.RemoteHermesRepository
import com.hermes.android.domain.repository.Result
import com.hermes.android.domain.repository.ConnectionTestResult
import javax.inject.Inject

class TestRemoteConnectionUseCase @Inject constructor(
    private val remoteHermesRepository: RemoteHermesRepository
) {
    operator fun invoke(config: RemoteConfig): Result<ConnectionTestResult> {
        return remoteHermesRepository.testConnection(config.baseUrl, config.apiKey)
    }
}