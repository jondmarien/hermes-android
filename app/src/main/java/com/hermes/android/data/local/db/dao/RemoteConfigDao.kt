package com.hermes.android.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hermes.android.domain.model.RemoteConfig
import kotlinx.coroutines.flow.Flow
import java.util.List

@Dao
interface RemoteConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: RemoteConfig): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(configs: List<RemoteConfig>): List<Long>

    @Update
    suspend fun update(config: RemoteConfig): Int

    @Delete
    suspend fun delete(config: RemoteConfig): Int

    @Query("DELETE FROM remote_configs WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM remote_configs WHERE id = :id")
    suspend fun getById(id: Long): RemoteConfig?

    @Query("SELECT * FROM remote_configs WHERE isDefault = 1")
    suspend fun getDefault(): RemoteConfig?

    @Query("SELECT * FROM remote_configs ORDER BY createdAt DESC")
    fun getAll(): Flow<List<RemoteConfig>>

    @Query("UPDATE remote_configs SET isDefault = 0 WHERE isDefault = 1")
    suspend fun clearDefault(): Int

    @Query("UPDATE remote_configs SET isDefault = :isDefault WHERE id = :id")
    suspend fun setDefault(id: Long, isDefault: Boolean): Int
}