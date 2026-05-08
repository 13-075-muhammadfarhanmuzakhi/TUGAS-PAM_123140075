package com.aiassistant.app.data.repository
import com.aiassistant.app.data.models.*
import com.aiassistant.app.data.network.HttpClientFactory
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GeminiRepository {
    private val client = HttpClientFactory.client
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    private val model = "gemini-2.0-flash"

    suspend fun chat(
        apiKey: String,
        messages: List<ChatMessage>,
        systemPrompt: String
    ): Result<String> = runCatching {
        val allMessages = mutableListOf<GeminiContent>()
        if (systemPrompt.isNotEmpty()) {
            allMessages.add(GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)), role = "user"))
            allMessages.add(GeminiContent(parts = listOf(GeminiPart(text = "Understood!")), role = "model"))
        }
        allMessages.addAll(messages.map { msg ->
            GeminiContent(parts = listOf(GeminiPart(text = msg.content)), role = if (msg.isUser) "user" else "model")
        })
        val request = GeminiRequest(contents = allMessages, generationConfig = GeminiGenerationConfig())
        val response: GeminiResponse = client.post("$baseUrl/models/$model:generateContent") {
            contentType(ContentType.Application.Json)
            parameter("key", apiKey)
            setBody(request)
        }.body()
        if (response.error != null) throw Exception("API Error ${response.error.code}: ${response.error.message}")
        response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: throw Exception("No response")
    }
}