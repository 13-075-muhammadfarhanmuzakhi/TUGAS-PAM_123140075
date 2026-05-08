package com.aiassistant.app.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String = System.nanoTime().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false
)

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerialName("generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @SerialName("systemInstruction") val systemInstruction: GeminiContent? = null
)

@Serializable
data class GeminiContent(val parts: List<GeminiPart>, val role: String = "user")

@Serializable
data class GeminiPart(val text: String)

@Serializable
data class GeminiGenerationConfig(
    val temperature: Double = 0.7,
    val maxOutputTokens: Int = 2048,
    @SerialName("topP") val topP: Double = 0.95
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate> = emptyList(),
    val error: GeminiError? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent,
    @SerialName("finishReason") val finishReason: String? = null
)

@Serializable
data class GeminiError(val code: Int = 0, val message: String = "", val status: String = "")