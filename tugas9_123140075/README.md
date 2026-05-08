# Tugas 9 - Integrasi AI API ke Aplikasi Mobile

| | |
|---|---|
| **Nama** | Muhammad Farhan Muzakhi |
| **NIM** | 123140075 |
| **Mata Kuliah** | Pengembangan Aplikasi Mobile |
| **Program Studi** | Teknik Informatika |
| **Institusi** | Institut Teknologi Sumatera (ITERA) |
| **Tahun Akademik** | Genap 2025/2026 |

---

## Deskripsi Aplikasi

**AI Assistant App** adalah aplikasi mobile Android yang mengintegrasikan kecerdasan buatan (AI) melalui Groq API. Aplikasi ini memungkinkan pengguna untuk berinteraksi dengan AI secara real-time melalui antarmuka chat yang modern dan responsif.

---

## Fitur Utama

- **Smart Chatbot** - Percakapan natural dengan AI menggunakan model Llama 3.1
- **Multi-turn Conversation** - AI mengingat konteks percakapan sebelumnya
- **System Prompt** - Kustomisasi perilaku dan kepribadian AI
- **Dark / Light Mode** - Tampilan yang nyaman di berbagai kondisi
- **Loading Indicator** - Animasi typing saat AI memproses jawaban
- **Error Handling** - Penanganan error yang informatif
- **Clear Chat** - Hapus riwayat percakapan kapan saja

---

## Teknologi yang Digunakan

| Teknologi | Kegunaan |
|---|---|
| Kotlin | Bahasa pemrograman utama |
| Jetpack Compose | UI framework modern |
| Ktor Client | HTTP networking |
| Groq API | AI inference (llama-3.1-8b-instant) |
| DataStore Preferences | Penyimpanan pengaturan |
| MVVM Architecture | Arsitektur aplikasi |
| Kotlin Coroutines | Asynchronous programming |
| Material Design 3 | Desain antarmuka |

---

## Arsitektur

\\\
app/
├── data/
│   ├── models/         # Data class (ChatMessage, dll)
│   ├── network/        # HttpClientFactory
│   ├── preferences/    # AppPreferences (DataStore)
│   └── repository/     # GroqRepository
├── ui/
│   ├── screens/        # ChatScreen, HomeScreen, SettingsScreen
│   ├── viewmodel/      # ChatViewModel
│   └── theme/          # Theme, Colors
└── navigation/         # AppNavigation
\\\

---

## Setup & Cara Menjalankan

### Prasyarat
- Android Studio Hedgehog atau lebih baru
- JDK 11+
- Android SDK API 26+

### Langkah Instalasi

1. Clone repository ini
\\\ash
git clone https://github.com/13-075-muhammadfarhanmuzakhi/TUGAS-PAM_123140075.git
\\\

2. Buka folder \	ugas9_123140075\ di Android Studio

3. Buat file \local.properties\ dan tambahkan API key
\\\
sdk.dir=YOUR_ANDROID_SDK_PATH
GROQ_API_KEY=YOUR_GROQ_API_KEY
\\\

4. Dapatkan API key gratis di [console.groq.com](https://console.groq.com)

5. Build dan jalankan aplikasi

---

## Screenshot

> Jalankan aplikasi dan masukkan API key Groq di halaman Settings untuk mulai menggunakan AI Assistant.

---

## Referensi

- [Groq API Documentation](https://console.groq.com/docs)
- [Ktor Client Documentation](https://ktor.io/docs/client-create-new-application.html)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Materi Pertemuan 9 - Integrasi AI API (ITERA)](https://itera.ac.id)