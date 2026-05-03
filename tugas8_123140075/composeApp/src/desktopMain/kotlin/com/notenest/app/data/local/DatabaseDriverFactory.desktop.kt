package com.notenest.app.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.notenest.app.db.NoteNestDatabase
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbDir = File(System.getProperty("user.home"), ".notenest")
        dbDir.mkdirs()
        val dbFile = File(dbDir, "notenest.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        try { NoteNestDatabase.Schema.create(driver) } catch (_: Exception) {}
        return driver
    }
}