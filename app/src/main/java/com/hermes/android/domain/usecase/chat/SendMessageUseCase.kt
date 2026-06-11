package com.hermes.android.domain.usecase.chat

import com.hermes.android.data.local.termux.TermuxManager
import com.hermes.android.domain.model.Chat
import com.hermes.android.domain.model.ConnectionMode
import com.hermes.android.domain.model.Session
import com.hermes.android.domain.repository.ChatRepository
import com.hermes.android.domain.repository.RemoteHermesRepository
import com.hermes.android.domain.repository.Result
import com.hermes.android.domain.repository.SessionRepository
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val sessionRepository: SessionRepository,
    private val remoteHermesRepository: RemoteHermesRepository,
    private val termuxManager: TermuxManager
) {
    operator fun invoke(
        sessionId: Long,
        content: String,
        mode: ConnectionMode,
        remoteConfig: com.hermes.android.domain.model.RemoteConfig?,
        model: String? = null
    ) = flow<Result<Chat>> {
        // Create user message
        val userMessage = Chat.createUserMessage(sessionId, content)
        val userMessageId = chatRepository.create(userMessage)
        val userMessageWithId = userMessage.copy(id = userMessageId)
        emit(Result.Success(userMessageWithId))

        // Update session message count
        sessionRepository.incrementMessageCount(sessionId, content.take(100))

        // Create assistant message placeholder
        val assistantMessage = Chat.createAssistantMessage(sessionId, "", model)
        val assistantMessageId = chatRepository.create(assistantMessage)
        var assistantMessageWithId = assistantMessage.copy(id = assistantMessageId)
        emit(Result.Success(assistantMessageWithId.copy(isStreaming = true)))

        when (mode) {
            ConnectionMode.REMOTE -> {
                remoteConfig?.let { config ->
                    val messages = buildMessageHistory(sessionId, userMessageWithId)
                    val streamResult = remoteHermesRepository.streamMessage(
                        baseUrl = config.baseUrl,
                        apiKey = config.apiKey,
                        messages = messages,
                        model = model
                    )

                    var fullContent = ""
                    streamResult.collect { result ->
                        when (result) {
                            is Result.Success -> {
                                val chunk = result.value
                                chunk.choices.firstOrNull()?.delta?.content?.let { delta ->
                                    fullContent += delta
                                    assistantMessageWithId = assistantMessageWithId.copy(
                                        content = fullContent,
                                        isStreaming = true
                                    )
                                    chatRepository.updateContent(
                                        assistantMessageWithId.id,
                                        fullContent,
                                        true
                                    )
                                    emit(Result.Success(assistantMessageWithId))
                                }
                                chunk.choices.firstOrNull()?.finishReason?.let { _ ->
                                    // Stream finished
                                    assistantMessageWithId = assistantMessageWithId.copy(
                                        content = fullContent,
                                        isStreaming = false,
                                        finishReason = chunk.choices.firstOrNull()?.finishReason
                                    )
                                    chatRepository.update(assistantMessageWithId)
                                    emit(Result.Success(assistantMessageWithId))
                                }
                            }
                            is Result.Error -> {
                                assistantMessageWithId = assistantMessageWithId.copy(
                                    content = fullContent + "\n\nError: ${result.exception.message}",
                                    isStreaming = false,
                                    error = result.exception.message
                                )
                                chatRepository.setError(assistantMessageWithId.id, result.exception.message!!)
                                emit(Result.Error(result.exception))
                            }
                        }
                    }
                } ?: emit(Result.Error(IllegalStateException("No remote config for REMOTE mode")))
            }
            ConnectionMode.LOCAL -> {
                // Send to Termux
                val termuxResult = termuxManager.sendCommand(
                    command = "hermes chat -q \"$content\"",
                    sessionId = sessionId
                )
                termuxResult.onSuccess { output ->
                    assistantMessageWithId = assistantMessageWithId.copy(
                        content = output,
                        isStreaming = false
                    )
                    chatRepository.update(assistantMessageWithId)
                    emit(Result.Success(assistantMessageWithId))
                }.onFailure { e ->
                    assistantMessageWithId = assistantMessageWithId.copy(
                        content = "Error: ${e.message}",
                        isStreaming = false,
                        error = e.message
                    )
                    chatRepository.setError(assistantMessageWithId.id, e.message!!)
                    emit(Result.Error(e))
                }
            }
        }
    }.flowOn(kotlinx.coroutines.Dispatchers.IO)

    private fun buildMessageHistory(sessionId: Long, userMessage: Chat): List<com.hermes.android.domain.repository.ChatMessage> {
        val messages = chatRepository.getBySessionIdPaged(sessionId, 50, 0)
        return messages.map { chat ->
            com.hermes.android.domain.repository.ChatMessage(
                role = chat.role.name.lowercase(),
                content = chat.content
            )
        }
    }
}