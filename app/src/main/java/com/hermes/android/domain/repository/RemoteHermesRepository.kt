package com.hermes.android.domain.repository

import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.domain.model.TermuxStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
    val name: String? = null
)

@Serializable
data class ChatCompletionResponse(
    val id: String,
    @SerialName("object") val objectType: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage?
)

@Serializable
data class Choice(
    val index: Int,
    val message: ChatMessage,
    val finishReason: String?
)

@Serializable
data class Usage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)

@Serializable
data class StreamChunk(
    val id: String,
    @SerialName("object") val objectType: String,
    val created: Long,
    val model: String,
    val choices: List<StreamChoice>
)

@Serializable
data class StreamChoice(
    val index: Int,
    val delta: Delta,
    val finishReason: String?
)

@Serializable
data class Delta(
    val role: String?,
    val content: String?
)

@Serializable
data class ResponsesApiResponse(
    val id: String,
    @SerialName("object") val objectType: String,
    val status: String,
    val model: String,
    val output: List<OutputItem>,
    val usage: Usage?
)

@Serializable
sealed class OutputItem {
    @Serializable
    data class Message(
        @SerialName("type") val type_: String,
        val role: String,
        val content: List<ContentPart>
    ) : OutputItem()

    @Serializable
    data class FunctionCall(
        @SerialName("type") val type_: String,
        val name: String,
        val arguments: String,
        val callId: String
    ) : OutputItem()

    @Serializable
    data class FunctionCallOutput(
        @SerialName("type") val type_: String,
        val callId: String,
        val output: String
    ) : OutputItem()
}

@Serializable
data class ContentPart(
    @SerialName("type") val type_: String,
    val text: String?
)

@Serializable
data class ConnectionTestResult(
    val success: Boolean,
    val latencyMs: Long,
    val version: String?,
    val error: String?
)

@Serializable
data class ModelInfo(
    val id: String,
    @SerialName("object") val objectType: String,
    val ownedBy: String
)

@Serializable
data class Capabilities(
    @SerialName("object") val objectType: String,
    val platform: String,
    val model: String,
    val auth: AuthInfo,
    val features: Features
)

@Serializable
data class AuthInfo(
    val type: String,
    val required: Boolean
)

@Serializable
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