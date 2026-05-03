package com.notenest.app.data.local

import app.cash.sqldelight.db.SqlDriver
import com.notenest.app.db.NoteNestDatabase

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

object DatabaseProvider {
    private var instance: NoteNestDatabase? = null

    fun getDatabase(factory: DatabaseDriverFactory): NoteNestDatabase {
        return instance ?: NoteNestDatabase(factory.createDriver()).also { instance = it }
    }
}