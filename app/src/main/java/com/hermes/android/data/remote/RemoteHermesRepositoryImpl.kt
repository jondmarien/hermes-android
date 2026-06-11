package com.hermes.android.data.remote

import com.hermes.android.data.remote.api.HermesApiService
import com.hermes.android.data.remote.api.HermesHealthService
import com.hermes.android.data.remote.api.HermesResponsesApiService
import com.hermes.android.domain.repository.Capabilities
import com.hermes.android.domain.repository.ChatCompletionResponse
import com.hermes.android.domain.repository.ChatMessage
import com.hermes.android.domain.repository.ConnectionTestResult
import com.hermes.android.domain.repository.ModelInfo
import com.hermes.android.domain.repository.RemoteHermesRepository
import com.hermes.android.domain.repository.ResponsesApiResponse
import com.hermes.android.domain.repository.Result
import com.hermes.android.domain.repository.StreamChunk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.tryCatch
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteHermesRepositoryImpl @Inject constructor(
    private val apiService: HermesApiService,
    private val responsesApiService: HermesResponsesApiService,
    private val healthService: HermesHealthService,
    private val eventSourceListenerFactory: okhttp3.sse.EventSourceListener.Factory
) : RemoteHermesRepository {

    private val timeoutSeconds = 60L

    override suspend fun sendMessage(
        baseUrl: String,
        apiKey: String,
        messages: List<ChatMessage>,
        model: String?,
        stream: Boolean
    ): Result<ChatCompletionResponse> {
        return try {
            val request = com.hermes.android.data.remote.api.ChatCompletionRequest(
                model = model ?: "hermes-agent",
                messages = messages,
                stream = stream
            )
            val response = withTimeoutOrNull(timeoutSeconds, TimeUnit.SECONDS) {
                apiService.chatCompletions("Bearer $apiKey", request)
            }
            response?.let { Result.Success(it) } ?: Result.Error(IOException("Request timeout"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun streamMessage(
        baseUrl: String,
        apiKey: String,
        messages: List<ChatMessage>,
        model: String?
    ) = flow<Result<StreamChunk>> {
        val request = com.hermes.android.data.remote.api.ChatCompletionRequest(
            model = model ?: "hermes-agent",
            messages = messages,
            stream = true
        )

        val json = com.google.gson.Gson().toJson(request)
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val eventSource = EventSources.createFactory(eventSourceListenerFactory)
            .newEventSource(
                okhttp3.Request.Builder()
                    .url("$baseUrl/v1/chat/completions")
                    .header("Authorization", "Bearer $apiKey")
                    .header("Accept", "text/event-stream")
                    .post(requestBody)
                    .build(),
                object : EventSourceListener() {
                    override fun onOpen(eventSource: EventSource?, response: okhttp3.Response?) {
                        // Stream opened
                    }

                    override fun onEvent(
                        eventSource: EventSource?,
                        id: String?,
                        type: String?,
                        data: String?
                    ) {
                        if (data == "[DONE]") {
                            return
                        }
                        try {
                            val chunk = com.google.gson.Gson().fromJson(data, StreamChunk::class.java)
                            emit(Result.Success(chunk))
                        } catch (e: Exception) {
                            emit(Result.Error(e))
                        }
                    }

                    override fun onClosed(eventSource: EventSource?) {
                        // Stream closed
                    }

                    override fun onFailure(
                        eventSource: EventSource?,
                        t: Throwable?,
                        response: okhttp3.Response?
                    ) {
                        t?.let { emit(Result.Error(it)) }
                    }
                }
            )

        // Wait for stream to complete
        try {
            // The flow will complete when the event source closes
        } finally {
            eventSource.cancel()
        }
    }.flowOn(kotlinx.coroutines.Dispatchers.IO)
        .tryCatch { e -> emit(Result.Error(e)) }

    override suspend fun sendResponse(
        baseUrl: String,
        apiKey: String,
        input: String,
        instructions: String?,
        previousResponseId: String?,
        conversation: String?,
        store: Boolean
    ): Result<ResponsesApiResponse> {
        return try {
            val request = com.hermes.android.data.remote.api.ResponsesRequest(
                model = "hermes-agent",
                input = input,
                instructions = instructions,
                previousResponseId = previousResponseId,
                conversation = conversation,
                store = store
            )
            val response = withTimeoutOrNull(timeoutSeconds, TimeUnit.SECONDS) {
                responsesApiService.createResponse("Bearer $apiKey", request)
            }
            response?.let { Result.Success(it) } ?: Result.Error(IOException("Request timeout"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun testConnection(baseUrl: String, apiKey: String): Result<ConnectionTestResult> {
        return try {
            val startTime = System.currentTimeMillis()
            val response = withTimeoutOrNull(10, TimeUnit.SECONDS) {
                healthService.healthCheck()
            }
            val latency = System.currentTimeMillis() - startTime

            if (response != null && response.status == "ok") {
                Result.Success(ConnectionTestResult(
                    success = true,
                    latencyMs = latency,
                    version = response.version,
                    error = null
                ))
            } else {
                Result.Success(ConnectionTestResult(
                    success = false,
                    latencyMs = latency,
                    version = null,
                    error = "Health check failed: ${response?.status}"
                ))
            }
        } catch (e: Exception) {
            Result.Success(ConnectionTestResult(
                success = false,
                latencyMs = -1,
                version = null,
                error = e.message
            ))
        }
    }

    override suspend fun getModels(baseUrl: String, apiKey: String): Result<List<ModelInfo>> {
        return try {
            val response = withTimeoutOrNull(timeoutSeconds, TimeUnit.SECONDS) {
                apiService.listModels("Bearer $apiKey")
            }
            response?.data?.let { Result.Success(it) } ?: Result.Error(IOException("Empty models list"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getCapabilities(baseUrl: String, apiKey: String): Result<Capabilities> {
        return try {
            val response = withTimeoutOrNull(timeoutSeconds, TimeUnit.SECONDS) {
                apiService.getCapabilities("Bearer $apiKey")
            }
            response?.let { Result.Success(it) } ?: Result.Error(IOException("Empty capabilities"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}