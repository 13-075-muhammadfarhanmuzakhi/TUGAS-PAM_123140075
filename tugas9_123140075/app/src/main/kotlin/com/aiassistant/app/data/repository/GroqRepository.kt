package com.aiassistant.app.data.repository

import com.aiassistant.app.data.models.ChatMessage
import com.aiassistant.app.data.network.HttpClientFactory
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class GroqMessage(val role: String, val content: String)

@Serializable
data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>,
    @SerialName("max_tokens") val maxTokens: Int = 1024,
    val temperature: Double = 0.7
)

class GroqRepository {
    private val client = HttpClientFactory.client
    private val baseUrl = "https://api.groq.com/openai/v1"
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun chat(
        apiKey: String,
        messages: List<ChatMessage>,
        systemPrompt: String
    ): Result<String> = runCatching {
        val allMessages = mutableListOf<GroqMessage>()
        allMessages.add(GroqMessage(
            role = "system",
            content = if (systemPrompt.isNotEmpty()) systemPrompt else "You are a helpful assistant. Answer in the same language as the user."
        ))
        allMessages.addAll(messages.map { msg ->
            GroqMessage(role = if (msg.isUser) "user" else "assistant", content = msg.content)
        })
        val requestBody = GroqRequest(
            model = "llama-3.1-8b-instant",
            messages = allMessages
        )
        val rawText = client.post("$baseUrl/chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $apiKey")
            setBody(requestBody)
        }.bodyAsText()

        val jsonObj = json.parseToJsonElement(rawText).jsonObject
        jsonObj["choices"]?.jsonArray?.firstOrNull()
            ?.jsonObject?.get("message")
            ?.jsonObject?.get("content")
            ?.jsonPrimitive?.content
            ?: jsonObj["error"]?.jsonObject?.get("message")?.jsonPrimitive?.content
            ?: "No response: $rawText"
    }
}