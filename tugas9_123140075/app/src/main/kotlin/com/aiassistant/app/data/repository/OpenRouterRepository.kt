package com.aiassistant.app.data.repository

import com.aiassistant.app.data.network.HttpClientFactory
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class OpenRouterMessage(val role: String, val content: String)

@Serializable
data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    @SerialName("max_tokens") val maxTokens: Int = 1000
)

class OpenRouterRepository {
    private val client = HttpClientFactory.client
    private val baseUrl = "https://openrouter.ai/api/v1"

    suspend fun chat(
        apiKey: String,
        messages: List<com.aiassistant.app.data.models.ChatMessage>,
        systemPrompt: String
    ): Result<String> = runCatching {
        val allMessages = mutableListOf<OpenRouterMessage>()
        if (systemPrompt.isNotEmpty()) {
            allMessages.add(OpenRouterMessage(role = "system", content = systemPrompt))
        }
        allMessages.addAll(messages.map { msg ->
            OpenRouterMessage(
                role = if (msg.isUser) "user" else "assistant",
                content = msg.content
            )
        })

        val request = OpenRouterRequest(
            model = "meta-llama/llama-3.2-3b-instruct:free",
            messages = allMessages,
            maxTokens = 1000
        )

        val rawResponse: JsonObject = client.post("$baseUrl/chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $apiKey")
            header("HTTP-Referer", "https://github.com/aiassistant")
            header("X-Title", "AI Assistant App")
            setBody(request)
        }.body()

        rawResponse["choices"]?.jsonArray?.firstOrNull()
            ?.jsonObject?.get("message")
            ?.jsonObject?.get("content")
            ?.jsonPrimitive?.content
            ?: rawResponse["error"]?.jsonObject?.get("message")?.jsonPrimitive?.content
            ?: "No response from AI"
    }
}