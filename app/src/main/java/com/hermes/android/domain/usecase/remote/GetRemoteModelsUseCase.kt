package com.hermes.android.domain.usecase.remote

import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.domain.repository.RemoteHermesRepository
import com.hermes.android.domain.repository.Result
import com.hermes.android.domain.repository.ModelInfo
import javax.inject.Inject

class GetRemoteModelsUseCase @Inject constructor(
    private val remoteHermesRepository: RemoteHermesRepository
) {
    operator fun invoke(config: RemoteConfig): Result<List<ModelInfo>> {
        return remoteHermesRepository.getModels(config.baseUrl, config.apiKey)
    }
}