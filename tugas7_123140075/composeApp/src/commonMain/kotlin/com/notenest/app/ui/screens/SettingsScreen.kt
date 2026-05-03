package com.notenest.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.notenest.app.model.AppTheme
import com.notenest.app.model.SortOrder
import com.notenest.app.ui.components.parseColor
import com.notenest.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    val scrollState = rememberScrollState()
    val accentOptions = listOf("#6C63FF" to "Purple","#00BCD4" to "Teal","#FF7043" to "Orange","#4CAF50" to "Green","#E91E63" to "Pink","#2196F3" to "Blue")

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.width(220.dp).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(24.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surface, CircleShape)) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(16.dp))
            Text("Settings", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
            Text("Customize your experience", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        }
        VerticalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Column(modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(scrollState).padding(28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            SettingsSection(title = "\uD83C\uDFA8 Appearance") {
                SettingItem(label = "Theme", subtitle = "Choose app color scheme") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppTheme.entries.forEach { theme ->
                            FilterChip(selected = settings.theme == theme, onClick = { viewModel.setTheme(theme) },
                                label = { Text(theme.label, style = MaterialTheme.typography.labelMedium) },
                                leadingIcon = if (settings.theme == theme) { { Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp)) } } else null)
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Accent Color", subtitle = "Highlight color for UI elements") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        accentOptions.forEach { (hex, name) ->
                            val color = parseColor(hex); val isSelected = settings.accentColor == hex
                            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color)
                                .border(width = if (isSelected) 3.dp else 0.dp, color = MaterialTheme.colorScheme.onBackground, shape = CircleShape)
                                .clickable { viewModel.setAccentColor(hex) }, contentAlignment = Alignment.Center) {
                                if (isSelected) Icon(Icons.Default.Check, name, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
            SettingsSection(title = "\uD83D\uDCCB Notes") {
                SettingItem(label = "Default Sort Order", subtitle = "How notes are ordered") {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { expanded = true }, shape = RoundedCornerShape(10.dp)) {
                            Text(settings.sortOrder.label); Spacer(Modifier.width(4.dp)); Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            SortOrder.entries.forEach { order ->
                                DropdownMenuItem(text = { Text(order.label, style = MaterialTheme.typography.bodyMedium) },
                                    onClick = { viewModel.setSortOrder(order); expanded = false },
                                    leadingIcon = if (settings.sortOrder == order) { { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) } } else null)
                            }
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Compact List View", subtitle = "Show notes in condensed format") { Switch(checked = settings.compactView, onCheckedChange = { viewModel.setCompactView(it) }) }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Show Pinned First", subtitle = "Display pinned notes at the top") { Switch(checked = settings.showPinnedFirst, onCheckedChange = { viewModel.setShowPinnedFirst(it) }) }
            }
            SettingsSection(title = "\u2139\uFE0F About") {
                SettingItem(label = "NoteNest", subtitle = "Pengembangan Aplikasi Mobile - ITERA") {
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)) {
                        Text("v1.0.0", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Storage", subtitle = "SQLDelight + Multiplatform Settings") { Icon(Icons.Outlined.Storage, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Platform", subtitle = "Kotlin Multiplatform + Compose Desktop") { Icon(Icons.Outlined.DesktopWindows, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 8.dp))
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))) {
            Column(modifier = Modifier.padding(4.dp)) { content() }
        }
    }
}

@Composable
fun SettingItem(label: String, subtitle: String, control: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
        control()
    }
}