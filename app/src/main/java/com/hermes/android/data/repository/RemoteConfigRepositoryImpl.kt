package com.hermes.android.data.repository

import com.hermes.android.data.local.db.dao.RemoteConfigDao
import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteConfigRepositoryImpl @Inject constructor(
    private val remoteConfigDao: RemoteConfigDao
) : RemoteConfigRepository {

    override suspend fun create(config: RemoteConfig): Long {
        return remoteConfigDao.insert(config)
    }

    override suspend fun getById(id: Long): RemoteConfig? {
        return remoteConfigDao.getById(id)
    }

    override suspend fun getDefault(): RemoteConfig? {
        return remoteConfigDao.getDefault()
    }

    override fun getAll(): Flow<List<RemoteConfig>> {
        return remoteConfigDao.getAll()
    }

    override suspend fun update(config: RemoteConfig) {
        remoteConfigDao.update(config)
    }

    override suspend fun setDefault(id: Long) {
        remoteConfigDao.clearDefault()
        remoteConfigDao.setDefault(id, true)
    }

    override suspend fun delete(id: Long) {
        remoteConfigDao.deleteById(id)
    }
}