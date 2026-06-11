package com.hermes.android.domain.repository

import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.domain.model.TermuxStatus
import kotlinx.coroutines.flow.Flow

interface RemoteHermesRepository {
    suspend fun sendMessage(
        baseUrl: String,
        apiKey: String,
        messages: List<ChatMessage>,
        model: String?,
        stream: Boolean
    ): Result<ChatCompletionResponse>

    suspend fun streamMessage(
        baseUrl: String,
        apiKey: String,
        messages: List<ChatMessage>,
        model: String?
    ): Flow<Result<StreamChunk>>

    suspend fun sendResponse(
        baseUrl: String,
        apiKey: String,
        input: String,
        instructions: String?,
        previousResponseId: String?,
        conversation: String?,
        store: Boolean
    ): Result<ResponsesApiResponse>

    suspend fun testConnection(baseUrl: String, apiKey: String): Result<ConnectionTestResult>

    suspend fun getModels(baseUrl: String, apiKey: String): Result<List<ModelInfo>>

    suspend fun getCapabilities(baseUrl: String, apiKey: String): Result<Capabilities>
}

@kotlinx.serialization.Serializable
data class ChatMessage(
    val role: String,
    val content: String,
    val name: String? = null
)

@kotlinx.serialization.Serializable
data class ChatCompletionResponse(
    val id: String,
    val object: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage?
)

@kotlinx.serialization.Serializable
data class Choice(
    val index: Int,
    val message: ChatMessage,
    val finishReason: String?
)

@kotlinx.serialization.Serializable
data class Usage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)

@kotlinx.serialization.Serializable
data class StreamChunk(
    val id: String,
    val object: String,
    val created: Long,
    val model: String,
    val choices: List<StreamChoice>
)

@kotlinx.serialization.Serializable
data class StreamChoice(
    val index: Int,
    val delta: Delta,
    val finishReason: String?
)

@kotlinx.serialization.Serializable
data class Delta(
    val role: String?,
    val content: String?
)

@kotlinx.serialization.Serializable
data class ResponsesApiResponse(
    val id: String,
    val object: String,
    val status: String,
    val model: String,
    val output: List<OutputItem>,
    val usage: Usage?
)

@kotlinx.serialization.Serializable
sealed class OutputItem {
    @kotlinx.serialization.Serializable
    data class Message(
        val type: String,
        val role: String,
        val content: List<ContentPart>
    ) : OutputItem()

    @kotlinx.serialization.Serializable
    data class FunctionCall(
        val type: String,
        val name: String,
        val arguments: String,
        val callId: String
    ) : OutputItem()

    @kotlinx.serialization.Serializable
    data class FunctionCallOutput(
        val type: String,
        val callId: String,
        val output: String
    ) : OutputItem()
}

@kotlinx.serialization.Serializable
data class ContentPart(
    val type: String,
    val text: String?
)

@kotlinx.serialization.Serializable
data class ConnectionTestResult(
    val success: Boolean,
    val latencyMs: Long,
    val version: String?,
    val error: String?
)

@kotlinx.serialization.Serializable
data class ModelInfo(
    val id: String,
    val object: String,
    val ownedBy: String
)

@kotlinx.serialization.Serializable
data class Capabilities(
    val object: String,
    val platform: String,
    val model: String,
    val auth: AuthInfo,
    val features: Features
)

@kotlinx.serialization.Serializable
data class AuthInfo(
    val type: String,
    val required: Boolean
)

@kotlinx.serialization.Serializable
data class Features(
    val chatCompletions: Boolean,
    val responsesApi: Boolean,
    val runSubmission: Boolean,
    val runStatus: Boolean,
    val runEventsSse: Boolean,
    val runStop: Boolean
)

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}