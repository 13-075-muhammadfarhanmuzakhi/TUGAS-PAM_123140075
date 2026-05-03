package com.notenest.app.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.notenest.app.model.Note
import com.notenest.app.model.NoteCategory
import com.notenest.app.model.noteColorPalette

@Composable
fun NoteEditorDialog(note: Note? = null, onDismiss: () -> Unit,
    onSave: (title: String, content: String, category: NoteCategory, colorHex: String, isPinned: Boolean) -> Unit) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var selectedCategory by remember { mutableStateOf(note?.category ?: NoteCategory.GENERAL) }
    var selectedColor by remember { mutableStateOf(note?.colorHex ?: noteColorPalette.first()) }
    var isPinned by remember { mutableStateOf(note?.isPinned ?: false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    val isEditing = note != null

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isEditing) "Edit Note" else "New Note", style = MaterialTheme.typography.headlineMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { isPinned = !isPinned }) {
                            Icon(imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Default.PushPin, contentDescription = "Pin",
                                tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        }
                        IconButton(onClick = onDismiss) { Icon(Icons.Outlined.Close, "Close") }
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Note title...") }, shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.titleLarge, singleLine = true)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = content, onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 240.dp),
                    placeholder = { Text("Write your note here...") }, shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Category", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Box {
                        OutlinedButton(onClick = { showCategoryDropdown = true }, shape = RoundedCornerShape(10.dp)) {
                            Text("${selectedCategory.emoji} ${selectedCategory.label}")
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(expanded = showCategoryDropdown, onDismissRequest = { showCategoryDropdown = false }) {
                            NoteCategory.entries.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) { Text(cat.emoji); Text(cat.label) } },
                                    onClick = { selectedCategory = cat; showCategoryDropdown = false },
                                    leadingIcon = if (selectedCategory == cat) { { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) } } else null)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Color", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        noteColorPalette.forEach { colorHex ->
                            val color = parseColor(colorHex); val isSelected = selectedColor == colorHex
                            Box(modifier = Modifier.size(28.dp).clip(CircleShape)
                                .background(if (colorHex == "#FFFFFF") MaterialTheme.colorScheme.surfaceVariant else color)
                                .border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), shape = CircleShape)
                                .clickable { selectedColor = colorHex }, contentAlignment = Alignment.Center) {
                                if (isSelected) Icon(Icons.Default.Check, null, tint = if (colorHex == "#FFFFFF") MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Cancel") }
                    Button(onClick = { if (title.isNotBlank() || content.isNotBlank()) { onSave(title, content, selectedCategory, selectedColor, isPinned); onDismiss() } },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = title.isNotBlank() || content.isNotBlank()) {
                        Icon(if (isEditing) Icons.Default.Save else Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (isEditing) "Update" else "Save")
                    }
                }
            }
        }
    }
}