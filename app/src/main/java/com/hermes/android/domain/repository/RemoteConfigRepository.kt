package com.hermes.android.domain.repository

import com.hermes.android.domain.model.RemoteConfig
import kotlinx.coroutines.flow.Flow

interface RemoteConfigRepository {
    suspend fun create(config: RemoteConfig): Long
    suspend fun getById(id: Long): RemoteConfig?
    suspend fun getDefault(): RemoteConfig?
    fun getAll(): Flow<List<RemoteConfig>>
    suspend fun update(config: RemoteConfig)
    suspend fun setDefault(id: Long)
    suspend fun delete(id: Long)
}