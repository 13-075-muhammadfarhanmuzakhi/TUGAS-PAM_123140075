package com.notenest.app.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.notenest.app.model.Note

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(note: Note, onClick: () -> Unit, onDelete: () -> Unit,
             onTogglePin: () -> Unit, compact: Boolean = false, modifier: Modifier = Modifier) {
    val noteColor = parseColor(note.colorHex)

    Card(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
        .combinedClickable(onClick = onClick, onLongClick = {}),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = noteColor.copy(alpha = 0.15f).compositeOver(MaterialTheme.colorScheme.surface)),
        border = BorderStroke(width = if (note.isPinned) 2.dp else 1.dp,
            color = if (note.isPinned) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (note.isPinned) 4.dp else 1.dp, hoveredElevation = 6.dp)) {
        Column(modifier = Modifier.padding(if (compact) 12.dp else 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                CategoryChip(emoji = note.category.emoji, label = note.category.label, color = noteColor)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.isPinned) {
                        Icon(Icons.Filled.Star, "Pinned", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                    }
                    Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(6.dp))
                        .background(noteColor.takeIf { note.colorHex != "#FFFFFF" } ?: MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
                }
            }
            Spacer(Modifier.height(if (compact) 6.dp else 10.dp))
            if (note.title.isNotBlank()) {
                Text(text = note.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (compact) 1 else 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
            }
            if (note.content.isNotBlank() && !compact) {
                Text(text = note.preview, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(10.dp))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = note.formattedDate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                Row {
                    IconButton(onClick = onTogglePin, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = if (note.isPinned) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = if (note.isPinned) "Unpin" else "Pin",
                            tint = if (note.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(emoji: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.25f).compositeOver(MaterialTheme.colorScheme.surfaceVariant)) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = emoji, style = MaterialTheme.typography.labelSmall)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
    }
}

fun parseColor(hex: String): Color {
    return try {
        val cleaned = hex.trimStart('#')
        val argb = when (cleaned.length) {
            6 -> "FF$cleaned"
            8 -> cleaned
            else -> "FFFFFFFF"
        }
        Color(argb.toLong(16).toInt())
    } catch (e: Exception) {
        Color.White
    }
}

fun Color.compositeOver(background: Color): Color {
    val alpha = this.alpha
    return Color(
        red = this.red * alpha + background.red * (1 - alpha),
        green = this.green * alpha + background.green * (1 - alpha),
        blue = this.blue * alpha + background.blue * (1 - alpha),
        alpha = 1f
    )
}