package com.example.tugas6_123140075.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface NewsRepository {
    suspend fun getTopHeadlines(country: String = "us", category: String = "general"): Result<List<Article>>
    suspend fun searchNews(query: String): Result<List<Article>>
}

class NewsRepositoryImpl(
    private val apiKey: String,
    private val baseUrl: String
) : NewsRepository {

    private val client = HttpClient(Java) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("KtorClient: $message")
                }
            }
            level = LogLevel.NONE
        }
    }

    override suspend fun getTopHeadlines(country: String, category: String): Result<List<Article>> {
        return try {
            if (apiKey != "YOUR_API_KEY_HERE" && apiKey.isNotBlank()) {
                val response = client.get("${baseUrl}top-headlines") {
                    parameter("country", country)
                    parameter("category", category)
                    parameter("apiKey", apiKey)
                    parameter("pageSize", 20)
                }
                val newsResponse = Json { ignoreUnknownKeys = true; isLenient = true }
                    .decodeFromString<NewsResponse>(response.bodyAsText())
                Result.success(newsResponse.articles)
            } else {
                getFallbackNews()
            }
        } catch (e: Exception) {
            println("Error fetching headlines: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun searchNews(query: String): Result<List<Article>> {
        return try {
            if (apiKey != "YOUR_API_KEY_HERE" && apiKey.isNotBlank()) {
                val response = client.get("${baseUrl}everything") {
                    parameter("q", query)
                    parameter("apiKey", apiKey)
                    parameter("pageSize", 20)
                    parameter("sortBy", "publishedAt")
                }
                val newsResponse = Json { ignoreUnknownKeys = true; isLenient = true }
                    .decodeFromString<NewsResponse>(response.bodyAsText())
                Result.success(newsResponse.articles)
            } else {
                getFallbackNews()
            }
        } catch (e: Exception) {
            println("Error searching news: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun getFallbackNews(): Result<List<Article>> {
        return try {
            val response = client.get("https://jsonplaceholder.typicode.com/posts") {
                parameter("_limit", 20)
            }
            val posts = Json { ignoreUnknownKeys = true; isLenient = true }
                .decodeFromString<List<JsonPlaceholderPost>>(response.bodyAsText())

            val articles = posts.mapIndexed { index, post ->
                Article(
                    source = Source(id = null, name = "JSONPlaceholder"),
                    author = "Author ${post.userId}",
                    title = post.title.replaceFirstChar { it.uppercase() },
                    description = post.body,
                    url = "https://jsonplaceholder.typicode.com/posts/${post.id}",
                    urlToImage = "https://picsum.photos/seed/${post.id}/800/400",
                    publishedAt = "2024-01-${String.format("%02d", (index % 30) + 1)}T10:00:00Z",
                    content = post.body
                )
            }
            Result.success(articles)
        } catch (e: Exception) {
            println("Error fetching fallback: ${e.message}")
            Result.failure(e)
        }
    }
}

@Serializable
private data class JsonPlaceholderPost(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)