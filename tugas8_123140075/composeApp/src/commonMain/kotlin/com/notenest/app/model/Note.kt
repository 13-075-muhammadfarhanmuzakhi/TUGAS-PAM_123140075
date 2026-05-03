package com.notenest.app.model

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val isPinned: Boolean,
    val colorHex: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    val formattedDate: String
        get() {
            val instant = Instant.fromEpochMilliseconds(updatedAt)
            val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            val day = local.dayOfMonth.toString().padStart(2, '0')
            val month = local.monthNumber.toString().padStart(2, '0')
            val year = local.year
            val hour = local.hour.toString().padStart(2, '0')
            val minute = local.minute.toString().padStart(2, '0')
            return "$day/$month/$year $hour:$minute"
        }

    val preview: String
        get() = if (content.length > 120) content.take(120) + "..." else content
}

enum class NoteCategory(val label: String, val emoji: String) {
    GENERAL("General", "\uD83D\uDCDD"),
    WORK("Work", "\uD83D\uDCBC"),
    PERSONAL("Personal", "\uD83C\uDFE0"),
    STUDY("Study", "\uD83D\uDCDA"),
    IDEA("Ideas", "\uD83D\uDCA1"),
    HEALTH("Health", "\u2764\uFE0F");

    companion object {
        fun fromString(value: String): NoteCategory =
            entries.firstOrNull { it.name.lowercase() == value.lowercase() } ?: GENERAL
    }
}

val noteColorPalette = listOf(
    "#FFFFFF", "#FFF9C4", "#F8BBD9", "#C8E6C9",
    "#BBDEFB", "#E1BEE7", "#FFCCBC", "#B2EBF2"
)