package com.notenest.app.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.notenest.app.model.NoteCategory

@Composable
fun AppSidebar(selectedCategory: NoteCategory?, onCategorySelected: (NoteCategory?) -> Unit,
    onSettingsClick: () -> Unit, noteCounts: Map<String, Int>, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.fillMaxHeight().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        .padding(vertical = 24.dp, horizontal = 12.dp).verticalScroll(scrollState)) {
        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(38.dp)) {
                Box(contentAlignment = Alignment.Center) { Text("\uD83D\uDCD3", style = MaterialTheme.typography.titleLarge) }
            }
            Column {
                Text("NoteNest", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                Text("v1.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            }
        }
        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(Modifier.height(16.dp))
        Text("LIBRARY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
        Spacer(Modifier.height(4.dp))
        SidebarItem(icon = { Icon(Icons.Outlined.Notes, null, modifier = Modifier.size(18.dp)) }, label = "All Notes", count = noteCounts["total"] ?: 0, isSelected = selectedCategory == null, onClick = { onCategorySelected(null) })
        SidebarItem(icon = { Icon(Icons.Outlined.PushPin, null, modifier = Modifier.size(18.dp)) }, label = "Pinned", count = noteCounts["pinned"] ?: 0, isSelected = false, onClick = {})
        Spacer(Modifier.height(16.dp))
        Text("CATEGORIES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
        Spacer(Modifier.height(4.dp))
        NoteCategory.entries.forEach { category ->
            SidebarItem(icon = { Text(category.emoji, style = MaterialTheme.typography.bodyMedium) }, label = category.label,
                count = noteCounts[category.name] ?: 0, isSelected = selectedCategory == category, onClick = { onCategorySelected(category) })
        }
        Spacer(Modifier.weight(1f))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(Modifier.height(12.dp))
        SidebarItem(icon = { Icon(Icons.Outlined.Settings, null, modifier = Modifier.size(18.dp)) }, label = "Settings", count = null, isSelected = false, onClick = onSettingsClick)
    }
}

@Composable
fun SidebarItem(icon: @Composable () -> Unit, label: String, count: Int?, isSelected: Boolean, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else androidx.compose.ui.graphics.Color.Transparent,
        shape = RoundedCornerShape(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CompositionLocalProvider(LocalContentColor provides if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) { icon() }
            Text(text = label, style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            if (count != null && count > 0) {
                Surface(shape = RoundedCornerShape(10.dp), color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)) {
                    Text(text = count.toString(), style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }
    }
}