package com.example.tugas6_123140075.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    @SerialName("status") val status: String,
    @SerialName("totalResults") val totalResults: Int = 0,
    @SerialName("articles") val articles: List<Article> = emptyList()
)

@Serializable
data class Article(
    @SerialName("source") val source: Source? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: String? = null,
    @SerialName("url") val url: String = "",
    @SerialName("urlToImage") val urlToImage: String? = null,
    @SerialName("publishedAt") val publishedAt: String = "",
    @SerialName("content") val content: String? = null
)

@Serializable
data class Source(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String = ""
)