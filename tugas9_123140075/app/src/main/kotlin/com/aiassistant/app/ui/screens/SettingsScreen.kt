package com.aiassistant.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiassistant.app.data.preferences.AppPreferences
import com.aiassistant.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(prefs: AppPreferences) {
    val scope = rememberCoroutineScope()
    val isDark by prefs.isDarkMode.collectAsState(initial = false)
    val openRouterKey by prefs.openRouterApiKey.collectAsState(initial = "")
    val systemPrompt by prefs.systemPrompt.collectAsState(initial = "")
    val username by prefs.username.collectAsState(initial = "")

    var openRouterKeyField by remember { mutableStateOf("") }
    var systemPromptField by remember { mutableStateOf("") }
    var usernameField by remember { mutableStateOf("") }
    var showKey by remember { mutableStateOf(false) }
    var saveSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(openRouterKey) { if (openRouterKeyField.isBlank()) openRouterKeyField = openRouterKey }
    LaunchedEffect(systemPrompt) { if (systemPromptField.isBlank()) systemPromptField = systemPrompt }
    LaunchedEffect(username) { if (usernameField.isBlank()) usernameField = username }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("Settings", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
        Spacer(Modifier.height(12.dp))

        SettingSection(title = "Appearance", icon = Icons.Filled.Palette) {
            SettingToggleRow(
                icon = Icons.Filled.DarkMode,
                title = "Dark Mode",
                subtitle = "Switch between light and dark theme",
                checked = isDark,
                onCheckedChange = { scope.launch { prefs.setDarkMode(it) } }
            )
        }

        Spacer(Modifier.height(12.dp))

        SettingSection(title = "Profile", icon = Icons.Filled.Person) {
            OutlinedTextField(
                value = usernameField,
                onValueChange = { usernameField = it },
                label = { Text("Your Name") },
                leadingIcon = { Icon(Icons.Filled.Badge, null) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        Spacer(Modifier.height(12.dp))

        SettingSection(title = "API Configuration", icon = Icons.Filled.Key) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("OpenRouter API Key", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = openRouterKeyField,
                    onValueChange = { openRouterKeyField = it },
                    label = { Text("sk-or-v1-...") },
                    leadingIcon = { Icon(Icons.Filled.VpnKey, null, tint = Violet400) },
                    trailingIcon = {
                        IconButton(onClick = { showKey = !showKey }) {
                            Icon(if (showKey) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                        }
                    },
                    visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                Spacer(Modifier.height(6.dp))
                Text("Get free API key at openrouter.ai/keys", fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.height(12.dp))

        SettingSection(title = "AI Behavior", icon = Icons.Filled.Psychology) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("System Prompt", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = systemPromptField,
                    onValueChange = { systemPromptField = it },
                    placeholder = { Text("Describe how the AI should behave...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 5
                )
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = {
                    systemPromptField = "You are a helpful, friendly, and smart AI assistant. Answer clearly and concisely."
                }) {
                    Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Reset to default", fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        AnimatedVisibility(
            visible = saveSuccess,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald400.copy(0.15f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, null, tint = Emerald400)
                    Spacer(Modifier.width(10.dp))
                    Text("Settings saved successfully!", color = Emerald400, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                scope.launch {
                    prefs.setOpenRouterApiKey(openRouterKeyField)
                    prefs.setSystemPrompt(systemPromptField)
                    prefs.setUsername(usernameField)
                    saveSuccess = true
                    kotlinx.coroutines.delay(2500)
                    saveSuccess = false
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Filled.Save, null)
            Spacer(Modifier.width(8.dp))
            Text("Save Settings", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.AutoAwesome, null, tint = Violet400, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(6.dp))
                Text("AI Assistant App", fontWeight = FontWeight.Bold)
                Text("v1.0.0 - Built with Jetpack Compose + OpenRouter AI",
                    fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun SettingSection(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(0.15f)
            )
            content()
        }
    }
}

@Composable
fun SettingToggleRow(
    icon: ImageVector, title: String, subtitle: String,
    checked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary.copy(0.8f), modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = White, checkedTrackColor = Violet400))
    }
}