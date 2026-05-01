# 📓 Notes App — Tugas 5 PAM ITERA

Aplikasi manajemen catatan harian yang dibangun menggunakan **Compose Multiplatform (KMP)** untuk target Android dan Desktop.

Proyek ini merupakan bagian dari **Tugas 5** mata kuliah Pengembangan Aplikasi Mobile di Institut Teknologi Sumatera (ITERA).

---

## 👤 Identitas Mahasiswa

| Field | Detail |
|---|---|
| Nama | Muhammad Farhan Muzakhi |
| NIM | 123140075 |
| Program Studi | Teknik Informatika |
| Semester | 6 |

---

## ✨ Fitur Utama

- **Manajemen Catatan** — Menampilkan daftar catatan harian dengan antarmuka yang bersih dan responsif
- **Navigation Architecture** — Implementasi navigasi modern menggunakan `navigation-compose` yang mendukung perpindahan antar layar (Beranda, Profil, Favorit)
- **Custom Drawer** — Menu samping interaktif dengan identitas pengguna kustom
- **Bottom Navigation** — Navigasi cepat di bagian bawah layar untuk akses fitur utama
- **MVVM Pattern** — Menggunakan pola Model-View-ViewModel untuk pemisahan logika bisnis dan tampilan

---

## 🛠️ Tech Stack

| Kategori | Teknologi |
|---|---|
| Language | Kotlin |
| UI Framework | Compose Multiplatform (Material 2 & 3) |
| Navigation | Jetpack Navigation Compose |
| Lifecycle | AndroidX Lifecycle ViewModel & Runtime |
| Async | Kotlin Coroutines |
| Build | Gradle KTS + Version Catalog |

---

## 📂 Struktur Proyek

```
composeApp/
└── src/
    ├── commonMain/kotlin/
    │   ├── com.example.farhan_123140075/
    │   │   └── App.kt                  ← Entry point utama aplikasi
    │   ├── navigation/
    │   │   └── Screen.kt               ← Definisi route navigasi
    │   └── screens/
    │       ├── ProfileScreen.kt        ← Tampilan Profil
    │       └── ProfileViewModel.kt     ← Logika data Profil
    └── androidMain/                    ← Kode spesifik Android
build.gradle.kts                        ← Konfigurasi dependency & versi
```

---

## 🚀 Cara Menjalankan

### Prasyarat
- Android Studio Koala atau versi terbaru
- JDK 17 (disarankan)
- Plugin Kotlin Multiplatform terinstall di IDE

### Langkah

1. Clone repository ini
```bash
git clone https://github.com/13-075-muhammadfarhanmuzakhi/TUGAS-PAM_123140075.git
```

2. Buka project di Android Studio

3. Sync Gradle dan tunggu proses selesai

4. Jalankan aplikasi di emulator atau device Android

---

## 🎥 Demo

[Lihat video tampilan hasil aplikasi](https://drive.google.com/drive/u/1/folders/1BqfkPba2EULJhn_hJrpnrcYK44QHwp5Y)
