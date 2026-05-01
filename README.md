Aplikasi manajemen catatan harian yang dibangun menggunakan Compose Multiplatform untuk target Android dan iOS (KMP). Proyek ini merupakan bagian dari tugas 5 mata kuliah Pengembangan Aplikasi Mobile di Institut Teknologi Sumatera (ITERA).
👤 Identitas Mahasiswa
    Nama: Muhammad Farhan Muzakhi
    NIM: 123140075
    Program Studi: Teknik Informatika (Semester 6)
✨ Fitur Utama
    Manajemen Catatan: Menampilkan daftar catatan harian dengan antarmuka yang bersih dan responsif.
    Navigation Architecture: Implementasi navigasi modern menggunakan navigation-compose yang mendukung perpindahan antar layar (Beranda, Profil, Favorit).
    Custom Drawer: Menu samping interaktif dengan identitas pengguna kustom.
    Bottom Navigation: Navigasi cepat di bagian bawah layar untuk akses fitur utama.
    Architecture Pattern: Menggunakan pola MVVM (Model-View-ViewModel) untuk pemisahan logika bisnis dan tampilan yang lebih stabil.
🛠️ Stack Teknologi
Aplikasi ini menggunakan teknologi terbaru dalam ekosistem Kotlin:
    Language: Kotlin.
    UI Framework: Compose Multiplatform (Material 2).
    Navigation: Jetpack Navigation Compose.
    Lifecycle: AndroidX Lifecycle ViewModel & Runtime.
    Coroutines: Untuk manajemen proses asynchronous pada UI.
📂 Struktur Proyek
Plaintext

composeApp/
├── src/
│   ├── commonMain/kotlin/
│   │   ├── com.example.farhan_123140075/
│   │   │   └── App.kt             <-- Entry point utama aplikasi
│   │   ├── navigation/
│   │   │   └── Screen.kt          <-- Definisi route navigasi
│   │   ├── screens/
│   │   │   ├── ProfileScreen.kt   <-- Tampilan Profil
│   │   │   └── ProfileViewModel.kt <-- Logika data Profil
│   └── androidMain/               <-- Kode spesifik Android
└── build.gradle.kts               <-- Konfigurasi dependency & versi

🚀 Cara Menjalankan (Installation)
Prasyarat
    Android Studio (Koala atau versi terbaru).
    JDK 17 (disarankan).
    Plugin Kotlin Multiplatform terinstall di IDE.

video tampilan hasil : https://drive.google.com/drive/u/1/folders/1BqfkPba2EULJhn_hJrpnrcYK44QHwp5Y
