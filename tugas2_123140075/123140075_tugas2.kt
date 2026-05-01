    package com.example.tugas1_123140075
    import kotlinx.coroutines.*
    import kotlinx.coroutines.flow.*

    /**
     * TUGAS PRAKTIKUM PERTEMUAN 2
     * Nama: Muhammad Farhan Muzakhi
     * NIM: 123140075
     */

// 1. Data Model (Konsep Advanced Kotlin)
    data class Berita(val id: Int, val judul: String, val kategori: String)

    class NewsFeedManager {
        // 2. StateFlow: Management Status (Skor Rubrik 20%)
        private val _totalDibaca = MutableStateFlow(0)
        val totalDibaca: StateFlow<Int> = _totalDibaca.asStateFlow()

        // 3. Flow Builder: Mengalirkan data secara Asynchronous (Skor Rubrik 25%)
        fun ambilAliranBerita(): Flow<Berita> = flow {
            val daftarBerita = listOf(
                Berita(1, "Fitur Baru Kotlin 2.0", "Tech"),
                Berita(2, "Tips Hidup Sehat", "Health"),
                Berita(3, "Belajar Coroutines Itu Mudah", "Tech"),
                Berita(4, "Resep Masakan Nusantara", "Food"),
                Berita(5, "Masa Depan Android Development", "Tech")
            )

            for (item in daftarBerita) {
                delay(1000) // Simulasi delay 1 detik
                emit(item)  // Mengirimkan data berita
            }
        }.catch { e ->
            // Bonus: Error Handling (Skor +10%)
            println("Terjadi gangguan koneksi: ${e.message}")
        }

        // 4. Suspend Function & Dispatchers (Skor Rubrik 20%)
        suspend fun updateStatusBaca() {
            withContext(Dispatchers.Default) {
                _totalDibaca.value += 1
            }
        }
    }

    // 5. Coroutine Utama menggunakan runBlocking
    fun main() = runBlocking {
        val manager = NewsFeedManager()
        val target = "Tech"

        println("=== SIMULASI NEWS FEED NIM 123140075 ===")

        // Menjalankan koleksi StateFlow di Coroutine berbeda (launch)
        val jobMonitor = launch {
            manager.totalDibaca.collect { jml ->
                println("[NOTIFIKASI] Anda telah membaca $jml berita $target")
            }
        }

        // 6. Menggunakan Operators: Filter dan Map (Skor Rubrik 20%)
        manager.ambilAliranBerita()
            .filter { it.kategori == target }    // Menyaring berita kategori Tech saja
            .map { it.judul.uppercase() }        // Mengubah judul menjadi Huruf Besar
            .collect { judulBerita ->
                println("MENAMPILKAN: $judulBerita")
                manager.updateStatusBaca()       // Memperbarui status baca
            }

        delay(500) // Memberi waktu sebentar sebelum selesai
        jobMonitor.cancel() // Memberhentikan monitor status
        println("=== SIMULASI SELESAI ===")
    }
