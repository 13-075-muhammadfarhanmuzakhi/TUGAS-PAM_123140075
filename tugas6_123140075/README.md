# 📰 News Reader App - Tugas Praktikum Minggu 6

**Nama:** Muhammad Farhan Muzakhi
**NIM:** 123140075  
**Mata Kuliah:** Pengembangan Aplikasi Mobile_RA

---

## 📋 Deskripsi Aplikasi

Aplikasi **News Reader** berbasis **Kotlin Multiplatform + Compose Desktop** yang menampilkan daftar berita dari public API. Aplikasi ini dibuat sebagai tugas praktikum minggu ke-6 dengan topik Networking dan REST API menggunakan Ktor Client.

---

## 🌐 API yang Digunakan

### Primary: NewsAPI
- **URL:** `https://newsapi.org/v2/`
- **Endpoint:** `top-headlines`, `everything`
- **Dokumentasi:** [newsapi.org](https://newsapi.org)

### Fallback: JSONPlaceholder + Picsum Photos
- **URL:** `https://jsonplaceholder.typicode.com/posts`
- **Gambar:** `https://picsum.photos/seed/{id}/800/400`
- Digunakan ketika API key NewsAPI belum diset

---

## ✅ Fitur yang Diimplementasikan

| No | Fitur | Status |
|----|-------|--------|
| 1 | Fetch berita dari public API (JSONPlaceholder/NewsAPI) | ✅ |
| 2 | Tampilkan list artikel dengan title, description, image | ✅ |
| 3 | Detail screen saat artikel di-klik | ✅ |
| 4 | Refresh functionality (tombol refresh di toolbar) | ✅ |
| 5 | Loading state (CircularProgressIndicator) | ✅ |
| 6 | Success state (LazyColumn daftar artikel) | ✅ |
| 7 | Error state (pesan error + tombol retry) | ✅ |
| 8 | Repository pattern untuk API calls | ✅ |
| 9 | Search berita | ✅ (bonus) |

---

## 🏗️ Arsitektur Aplikasi

Aplikasi menggunakan **Repository Pattern** dengan struktur sebagai berikut:

```
composeApp/src/
├── commonMain/kotlin/com/example/tugas6_123140075/
│   ├── App.kt                    # Entry point Composable
│   ├── data/
│   │   ├── News.kt               # Data models (Article, Source, NewsResponse)
│   │   └── NewsRepository.kt     # Repository + Ktor HTTP Client
│   ├── navigation/
│   │   └── Screens.kt            # Route definitions
│   ├── screens/
│   │   ├── MainScreens.kt        # NavHost & navigation setup
│   │   └── NewsScreens.kt        # UI: List, Detail, Loading, Error
│   ├── tema/
│   │   ├── Color.kt              # Color palette
│   │   ├── Theme.kt              # MaterialTheme setup
│   │   └── Type.kt               # Typography
│   └── viewmodel/
│       └── NewsViewModel.kt      # ViewModel + StateFlow + UI State
└── jvmMain/kotlin/com/example/tugas6_123140075/
    └── main.kt                   # Desktop entry point
```

### Alur Data:
```
UI (Screen) → ViewModel → Repository → Ktor HTTP Client → API
                ↑                              ↓
           StateFlow ←←←←←←←← Result<List<Article>>
```

---
## 🛠️ Teknologi yang Digunakan

| Teknologi | Versi | Kegunaan |
|-----------|-------|----------|
| Kotlin Multiplatform | 2.0.21 | Framework utama |
| Compose Multiplatform | 1.7.3 | UI Framework |
| Ktor Client | 3.0.3 | HTTP Client untuk API calls |
| Kotlinx Serialization | 1.7.3 | JSON parsing |
| Coil | 3.0.4 | Image loading |
| Lifecycle ViewModel | 2.8.4 | State management |
| Navigation Compose | 2.8.0 | Navigasi antar screen |
| Kotlinx Coroutines | 1.9.0 | Async/concurrent programming |

---

## 🚀 Cara Menjalankan Aplikasi

### Prasyarat
- Android Studio / IntelliJ IDEA
- JDK 11 atau lebih baru

### Langkah-langkah

1. Clone repository ini:
```bash
2. Buka project di Android Studio

3. Sync Gradle:
```
File → Sync Project with Gradle Files
```

4. Jalankan aplikasi (pilih salah satu):

**Via Android Studio:**
- Pilih run configuration `composeApp [jvm]` di toolbar
- Klik tombol ▶️ Run

**Via Terminal:**
```bash
# Windows
$env:JAVA_HOME = "D:\D sementara\androidstudio\jbr"
.\gradlew :composeApp:run

# Mac/Linux
./gradlew :composeApp:run
```

### Menggunakan NewsAPI (Opsional)
Untuk menggunakan berita sungguhan dari NewsAPI:
1. Daftar di [newsapi.org](https://newsapi.org) dan dapatkan API key gratis
2. Buka file `NewsViewModel.kt`
3. Ganti `"YOUR_API_KEY_HERE"` dengan API key kamu:
```kotlin
private val repository: NewsRepository = NewsRepositoryImpl(
    apiKey = "API_KEY_KAMU_DI_SINI",
    baseUrl = "https://newsapi.org/v2/"
)
```

---

## 📐 Implementasi Teknis

### 1. Ktor HTTP Client Setup
```kotlin
private val client = HttpClient(Java) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true; isLenient = true })
    }
    install(Logging) { level = LogLevel.NONE }
}
```

### 2. Repository Pattern
```kotlin
interface NewsRepository {
    suspend fun getTopHeadlines(country: String, category: String): Result<List<Article>>
    suspend fun searchNews(query: String): Result<List<Article>>
}
```

### 3. UI State Management
```kotlin
sealed class NewsUiState {
    data object Loading : NewsUiState()
    data class Success(val articles: List<Article>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}
```

---

## 📹 Video Demo

> Video demo : https://drive.google.com/drive/u/0/folders/1PI_X6S9AR1RoL-z6F8Ybz4SuDfLiKRAG


## 📚 Referensi

- [Ktor Client Documentation](https://ktor.io/docs/client.html)
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- [JSONPlaceholder API](https://jsonplaceholder.typicode.com)
- [NewsAPI](https://newsapi.org)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
