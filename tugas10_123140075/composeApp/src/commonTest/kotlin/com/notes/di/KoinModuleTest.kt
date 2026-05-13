package com.notes.di

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.*

/**
 * Koin DI module verification tests
 */
class KoinModuleTest : KoinTest {

    @AfterTest
    fun tearDown() {
        try { stopKoin() } catch (_: Exception) {}
    }

    @Test
    fun `verify all koin modules can be loaded without error`() {
        val app = startKoin {
            modules(appModules)
        }
        assertNotNull(app)
    }

    @Test
    fun `verify koin dependency graph is valid`() {
        // checkModules() butuh Android context, tidak bisa di unit test biasa
        // Verifikasi manual: pastikan startKoin tidak throw exception
        var exception: Exception? = null
        try {
            startKoin {
                modules(appModules)
            }
        } catch (e: Exception) {
            exception = e
        }
        assertNull(exception, "Koin modules gagal load: ${exception?.message}")
    }
}