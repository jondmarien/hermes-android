package com.hermes.android.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hermes.android.domain.model.Session
import kotlinx.coroutines.flow.Flow
import java.util.List

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(session: Session): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(sessions: List<Session>): List<Long>

    @Update
    suspend fun update(session: Session): Int

    @Delete
    suspend fun delete(session: Session): Int

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getById(id: Long): Session?

    @Query("SELECT * FROM sessions WHERE isActive = 1 ORDER BY updatedAt DESC")
    fun getActiveSessions(): Flow<List<Session>>

    @Query("SELECT * FROM sessions ORDER BY updatedAt DESC")
    fun getAllSessions(): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE id = :id")
    fun getSessionFlow(id: Long): Flow<Session?>

    @Query("UPDATE sessions SET title = :title, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String, updatedAt: String): Int

    @Query("UPDATE sessions SET isActive = :isActive, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setActive(id: Long, isActive: Boolean, updatedAt: String): Int

    @Query("UPDATE sessions SET messageCount = messageCount + 1, lastMessagePreview = :preview, updatedAt = :updatedAt WHERE id = :id")
    suspend fun incrementMessageCount(id: Long, preview: String, updatedAt: String): Int

    @Query("DELETE FROM sessions WHERE isActive = 0 AND updatedAt < :cutoff")
    suspend fun cleanupOldInactive(cutoff: String): Int
}