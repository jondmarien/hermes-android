package com.hermes.android.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hermes.android.domain.model.Chat
import kotlinx.coroutines.flow.Flow
import java.util.List

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(chat: Chat): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(chats: List<Chat>): List<Long>

    @Update
    suspend fun update(chat: Chat): Int

    @Delete
    suspend fun delete(chat: Chat): Int

    @Query("DELETE FROM chats WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM chats WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: Long): Int

    @Query("SELECT * FROM chats WHERE id = :id")
    suspend fun getById(id: Long): Chat?

    @Query("SELECT * FROM chats WHERE sessionId = :sessionId ORDER BY createdAt ASC")
    fun getBySessionId(sessionId: Long): Flow<List<Chat>>

    @Query("SELECT * FROM chats WHERE sessionId = :sessionId ORDER BY createdAt ASC LIMIT :limit OFFSET :offset")
    suspend fun getBySessionIdPaged(sessionId: Long, limit: Int, offset: Int): List<Chat>

    @Query("SELECT * FROM chats WHERE sessionId = :sessionId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastMessage(sessionId: Long): Chat?

    @Query("SELECT COUNT(*) FROM chats WHERE sessionId = :sessionId")
    suspend fun countBySessionId(sessionId: Long): Int

    @Query("UPDATE chats SET content = :content, isStreaming = :isStreaming, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateContent(id: Long, content: String, isStreaming: Boolean, updatedAt: String): Int

    @Query("UPDATE chats SET isStreaming = :isStreaming WHERE id = :id")
    suspend fun setStreaming(id: Long, isStreaming: Boolean): Int

    @Query("UPDATE chats SET error = :error WHERE id = :id")
    suspend fun setError(id: Long, error: String): Int
}