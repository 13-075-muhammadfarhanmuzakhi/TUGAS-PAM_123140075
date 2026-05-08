package com.aiassistant.app.data.preferences
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ai_assistant_prefs")
class AppPreferences(private val context: Context) {
    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
        val OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
        val AI_PROVIDER = stringPreferencesKey("ai_provider")
        val SYSTEM_PROMPT = stringPreferencesKey("system_prompt")
        val USERNAME = stringPreferencesKey("username")
        const val DEFAULT_OPENROUTER_KEY = "sk-or-v1-GANTI_KEY_BARU"
    }
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: false }
    val geminiApiKey: Flow<String> = context.dataStore.data.map { it[GEMINI_API_KEY] ?: "" }
    val openaiApiKey: Flow<String> = context.dataStore.data.map { it[OPENAI_API_KEY] ?: "" }
    val openRouterApiKey: Flow<String> = context.dataStore.data.map { it[OPENROUTER_API_KEY] ?: DEFAULT_OPENROUTER_KEY }
    val aiProvider: Flow<String> = context.dataStore.data.map { it[AI_PROVIDER] ?: "openrouter" }
    val systemPrompt: Flow<String> = context.dataStore.data.map { it[SYSTEM_PROMPT] ?: "You are a helpful, friendly, and smart AI assistant. Answer clearly and concisely." }
    val username: Flow<String> = context.dataStore.data.map { it[USERNAME] ?: "User" }
    suspend fun setDarkMode(value: Boolean) = context.dataStore.edit { it[DARK_MODE] = value }
    suspend fun setGeminiApiKey(value: String) = context.dataStore.edit { it[GEMINI_API_KEY] = value }
    suspend fun setOpenAiApiKey(value: String) = context.dataStore.edit { it[OPENAI_API_KEY] = value }
    suspend fun setOpenRouterApiKey(value: String) = context.dataStore.edit { it[OPENROUTER_API_KEY] = value }
    suspend fun setAiProvider(value: String) = context.dataStore.edit { it[AI_PROVIDER] = value }
    suspend fun setSystemPrompt(value: String) = context.dataStore.edit { it[SYSTEM_PROMPT] = value }
    suspend fun setUsername(value: String) = context.dataStore.edit { it[USERNAME] = value }
}