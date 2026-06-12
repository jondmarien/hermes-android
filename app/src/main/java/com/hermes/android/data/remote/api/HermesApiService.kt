package com.hermes.android.data.remote.api

import com.hermes.android.domain.repository.Capabilities
import com.hermes.android.domain.repository.ChatCompletionResponse
import com.hermes.android.domain.repository.ChatMessage
import com.hermes.android.domain.repository.ConnectionTestResult
import com.hermes.android.domain.repository.ModelInfo
import com.hermes.android.domain.repository.ResponsesApiResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface HermesApiService {

    @POST("/v1/chat/completions")
    suspend fun chatCompletions(
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse

    @GET("/v1/models")
    suspend fun listModels(
        @Header("Authorization") authorization: String
    ): ModelsResponse

    @GET("/v1/capabilities")
    suspend fun getCapabilities(
        @Header("Authorization") authorization: String
    ): Capabilities
}

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val stream: Boolean = false,
    val temperature: Float? = null,
    val maxTokens: Int? = null
)

@Serializable
data class ModelsResponse(
    @SerialName("object") val objectType: String,
    val data: List<ModelInfo>
)

interface HermesResponsesApiService {

    @POST("/v1/responses")
    suspend fun createResponse(
        @Header("Authorization") authorization: String,
        @Body request: ResponsesRequest
    ): ResponsesApiResponse

    @GET("/v1/responses/{responseId}")
    suspend fun getResponse(
        @Header("Authorization") authorization: String,
        @Path("responseId") responseId: String
    ): ResponsesApiResponse

    @retrofit2.http.DELETE("/v1/responses/{responseId}")
    suspend fun deleteResponse(
        @Header("Authorization") authorization: String,
        @Path("responseId") responseId: String
    ): DeleteResponse
}

@Serializable
data class ResponsesRequest(
    val model: String,
    val input: String,
    val instructions: String? = null,
    val previousResponseId: String? = null,
    val conversation: String? = null,
    val store: Boolean = true,
    val stream: Boolean = false
)

@Serializable
data class DeleteResponse(
    val id: String,
    @SerialName("object") val objectType: String,
    val deleted: Boolean
)

interface HermesHealthService {

    @GET("/health")
    suspend fun healthCheck(): HealthResponse

    @GET("/health/ready")
    suspend fun readinessCheck(): HealthResponse

    @GET("/health/live")
    suspend fun livenessCheck(): HealthResponse
}

@Serializable
data class HealthResponse(
    val status: String,
    val version: String? = null,
    val timestamp: String? = null
)