package com.aiassistant.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.aiassistant.app.ui.theme.AIAssistantTheme
import com.aiassistant.app.navigation.AppNavigation
import com.aiassistant.app.data.preferences.AppPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs = AppPreferences(this)
            val isDark by prefs.isDarkMode.collectAsState(initial = false)
            AIAssistantTheme(darkTheme = isDark) {
                AppNavigation(prefs = prefs)
            }
        }
    }
}