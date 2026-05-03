# NoteNest 📓
**Tugas Praktikum Minggu 7 — Pengembangan Aplikasi Mobile**
Institut Teknologi Sumatera (ITERA) | Program Studi Teknik Informatika

---

## 👤 Identitas Mahasiswa

| Field | Info |
|-------|------|
| **Nama** | Muhammad Farhan Muzakhi |
| **NIM** | 123140075 |
| **Mata Kuliah** | Pengembangan Aplikasi Mobile |
| **Pertemuan** | 7 — Local Data Storage |
--

---

## 📋 Deskripsi Aplikasi

**NoteNest** adalah aplikasi catatan desktop berbasis **Kotlin Multiplatform + Compose Desktop** yang mengimplementasikan local data storage dengan **SQLDelight** dan **Multiplatform Settings**. Aplikasi ini berjalan langsung di desktop (Windows/Mac/Linux) tanpa emulator.

---

## ✅ Fitur yang Diimplementasikan

| No | Fitur | Status |
|----|-------|--------|
| 1 | SQLDelight database untuk menyimpan notes | ✅ Done |
| 2 | CRUD operations (Create, Read, Update, Delete) | ✅ Done |
| 3 | Search functionality untuk mencari notes | ✅ Done |
| 4 | Settings screen dengan DataStore — theme & sort order | ✅ Done |
| 5 | Offline-first: data tersimpan lokal | ✅ Done |
| 6 | UI states yang proper (loading, empty, content) | ✅ Done |
| **Bonus** | Grid & List layout toggle, Note pinning, Color coding, Category filter | ✅ Done |

---

## 🗄️ Database Schema

```sql
CREATE TABLE NoteEntity (
    id          TEXT     NOT NULL PRIMARY KEY,   -- UUID unik tiap note
    title       TEXT     NOT NULL,               -- Judul note
    content     TEXT     NOT NULL,               -- Isi note
    category    TEXT     NOT NULL DEFAULT 'general', -- Kategori (general/work/personal/study/idea/health)
    is_pinned   INTEGER  NOT NULL DEFAULT 0,     -- Pin status (0=false, 1=true)
    color_hex   TEXT     NOT NULL DEFAULT '#FFFFFF', -- Warna note (hex)
    created_at  INTEGER  NOT NULL,               -- Waktu dibuat (epoch ms)
    updated_at  INTEGER  NOT NULL                -- Waktu diupdate (epoch ms)
);

-- Index untuk performa query
CREATE INDEX idx_note_updated  ON NoteEntity(updated_at DESC);
CREATE INDEX idx_note_category ON NoteEntity(category);
```

### SQL Queries (Note.sq)

```sql
-- Ambil semua notes (pinned duluan, terbaru di atas)
selectAll:
SELECT * FROM NoteEntity ORDER BY is_pinned DESC, updated_at DESC;

-- Cari notes berdasarkan judul atau isi
searchNotes:
SELECT * FROM NoteEntity
WHERE title LIKE '%' || ? || '%' OR content LIKE '%' || ? || '%'
ORDER BY is_pinned DESC, updated_at DESC;

-- Insert note baru
insertNote:
INSERT INTO NoteEntity(id, title, content, category, is_pinned, color_hex, created_at, updated_at)
VALUES (?, ?, ?, ?, ?, ?, ?, ?);

-- Update note yang ada
updateNote:
UPDATE NoteEntity
SET title = ?, content = ?, category = ?, is_pinned = ?, color_hex = ?, updated_at = ?
WHERE id = ?;

-- Hapus note
deleteNote:
DELETE FROM NoteEntity WHERE id = ?;
```

---

## 🏗️ Arsitektur Aplikasi

Aplikasi menggunakan **Repository Pattern** dengan **Offline-First Architecture**:

```
UI (Compose)
    ↕ StateFlow / collectAsState
ViewModel (NotesViewModel, SettingsViewModel)
    ↕ Flow
Repository (NoteRepository)
    ↕ Flow + suspend fun
LocalDataSource (NoteLocalDataSource)
    ↕ SQLDelight Queries
SQLite Database (.notenest/notenest.db)

Settings:
SettingsManager (Multiplatform Settings)
    ↕ ObservableSettings → FlowSettings
Java Preferences API (Windows Registry)
```

---

## 📁 Struktur Project

```
NoteNest/
├── gradle/
│   ├── libs.versions.toml          ← Versi semua dependency
│   └── wrapper/
│       └── gradle-wrapper.properties
├── composeApp/
│   ├── build.gradle.kts            ← Dependencies + SQLDelight config
│   └── src/
│       ├── commonMain/
│       │   ├── kotlin/com/notenest/app/
│       │   │   ├── data/
│       │   │   │   ├── local/
│       │   │   │   │   ├── DatabaseProvider.kt     ← Singleton database
│       │   │   │   │   ├── NoteLocalDataSource.kt  ← Query wrapper
│       │   │   │   │   └── SettingsManager.kt      ← DataStore/Settings
│       │   │   │   └── repository/
│       │   │   │       └── NoteRepository.kt       ← Business logic
│       │   │   ├── model/
│       │   │   │   ├── Note.kt                     ← Domain model
│       │   │   │   └── AppSettings.kt              ← Settings model
│       │   │   ├── ui/
│       │   │   │   ├── components/                 ← Reusable UI components
│       │   │   │   ├── screens/                    ← MainScreen, SettingsScreen
│       │   │   │   └── theme/                      ← Theme, Typography
│       │   │   ├── viewmodel/
│       │   │   │   ├── NotesViewModel.kt
│       │   │   │   └── SettingsViewModel.kt
│       │   │   └── App.kt                          ← Root composable
│       │   └── sqldelight/com/notenest/app/db/
│       │       └── Note.sq                         ← SQL schema + queries
│       └── desktopMain/
│           └── kotlin/com/notenest/app/
│               ├── data/local/
│               │   └── DatabaseDriverFactory.desktop.kt ← SQLite driver
│               ├── IsSystemInDarkTheme.kt
│               └── main.kt                         ← Entry point
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🛠️ Tech Stack

| Library | Versi | Fungsi |
|---------|-------|--------|
| Kotlin Multiplatform | 2.0.21 | Cross-platform framework |
| Compose Multiplatform | 1.7.0 | UI framework |
| **SQLDelight** | **2.0.2** | **Type-safe SQL database** |
| **Multiplatform Settings** | **1.2.0** | **DataStore / Preferences** |
| kotlinx-datetime | 0.6.1 | Date/time handling |
| kotlinx-coroutines | 1.9.0 | Async & Flow |
| Koin | 4.0.0 | Dependency Injection |
| UUID (benasher44) | 0.8.4 | Generate unique ID |

---

## ▶️ Cara Menjalankan

### Requirements
- JDK 17+ (dari Android Studio / JBR)
- Gradle 8.9 (sudah include di wrapper)

### Run Desktop App
```powershell
# Windows — set JAVA_HOME dulu
$env:JAVA_HOME = "C:\Path\To\Your\jbr"

# Jalankan aplikasi
.\gradlew :composeApp:run --no-configuration-cache
```

### Lokasi Database
Data tersimpan otomatis di:
- **Windows:** `C:\Users\<nama>\.notenest\notenest.db`
- **Mac/Linux:** `~/.notenest/notenest.db`

---

## 🎥 Video Demo

▶️ **[Klik di sini untuk menonton Video Demo (45 detik)](https://drive.google.com/drive/folders/1Bs3dLOVXNy5mj0f9gMxyuYywJ1lFzpEJ?hl=ID)**

Video mendemonstrasikan:
- ✅ Create note baru dengan kategori & warna
- ✅ Read / menampilkan semua notes
- ✅ Update / edit note yang sudah ada
- ✅ Delete note dengan konfirmasi dialog
- ✅ Search / pencarian notes real-time
- ✅ Settings — ganti tema (Light/Dark/System)
- ✅ Settings — ganti sort order
- ✅ Data tersimpan lokal (offline mode)


---

## 💡 Implementasi Konsep Modul 7

### 1. SQLDelight — Type-Safe SQL
SQLDelight men-generate Kotlin code dari file `.sq`, sehingga semua query sudah divalidasi saat compile time.

```kotlin
// NoteLocalDataSource.kt — query otomatis ter-generate oleh SQLDelight
fun getAllNotes(): Flow<List<Note>> =
    queries.selectAll()
        .asFlow()
        .mapToList(Dispatchers.Default)
        .map { list -> list.map { it.toDomain() } }
```

### 2. Multiplatform Settings — DataStore Alternative
Menggantikan `SharedPreferences`/`DataStore` dengan API yang support KMP menggunakan `ObservableSettings` + `FlowSettings`.

```kotlin
// SettingsManager.kt — reactive settings dengan Flow
val themeFlow: Flow<String> = flowSettings.getStringFlow(KEY_THEME, AppTheme.SYSTEM.name)

suspend fun setTheme(theme: AppTheme) {
    flowSettings.putString(KEY_THEME, theme.name)
}
```

### 3. Repository Pattern
```kotlin
// NoteRepository.kt — abstraksi data layer
class NoteRepository(private val localDataSource: NoteLocalDataSource) {
    fun getAllNotes(sortOrder: SortOrder): Flow<List<Note>> =
        localDataSource.getAllNotes().map { notes -> sortNotes(notes, sortOrder) }
}
```

### 4. Offline-First Architecture
Database lokal selalu menjadi sumber utama data. UI selalu membaca dari local DB via `Flow`, sehingga otomatis update saat data berubah.

---

## 📝 Catatan Pengembangan

- Aplikasi ini hanya target **Desktop (JVM)** sesuai requirement tugas
- Database file `.db` dibuat otomatis di folder home user saat pertama kali dijalankan
- Settings tersimpan di **Java Preferences API** (Windows Registry di Windows, `~/Library/Preferences` di Mac)
- Icon menggunakan `compose.materialIconsExtended`
