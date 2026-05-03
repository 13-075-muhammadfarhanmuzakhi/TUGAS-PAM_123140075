package com.notenest.app.model

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val sortOrder: SortOrder = SortOrder.UPDATED_DESC,
    val showPinnedFirst: Boolean = true,
    val compactView: Boolean = false,
    val accentColor: String = "#6C63FF"
)

enum class AppTheme(val label: String) {
    LIGHT("Light"), DARK("Dark"), SYSTEM("System Default")
}

enum class SortOrder(val label: String) {
    UPDATED_DESC("Last Modified"), UPDATED_ASC("Oldest Modified"),
    CREATED_DESC("Newest Created"), CREATED_ASC("Oldest Created"),
    TITLE_ASC("Title A-Z"), TITLE_DESC("Title Z-A")
}