# 📝 NotesApp — Tugas Praktikum Minggu 10
**Nama:** Muhammad Farhan Muzakhi  
**NIM:** 123140075  
**Mata Kuliah:** Pengembangan Aplikasi Mobile  
**Topik:** Testing dan Dependency Injection  

---

## 📱 Demo Aplikasi

🎥 **Video Demo (45 detik):** [Klik di sini untuk menonton](https://drive.google.com/drive/folders/1PiZKZHsrEqiRNeI_4KSujU9CJjG--4rr?usp=sharing)

---

## ✅ Checklist Tugas

| Komponen | Bobot | Status |
|---|---|---|
| Koin DI Setup (2+ modules) | 20% | ✅ Done |
| Unit Test NoteRepository (5+ cases) | 20% | ✅ Done |
| Unit Test NotesViewModel + MockK (4+ cases) | 20% | ✅ Done |
| Flow Test dengan Turbine (2+ cases) | 15% | ✅ Done |
| UI Test NotesScreen (3+ cases) | 15% | ✅ Done |
| Code Quality (AAA pattern) | 10% | ✅ Done |

---

## 🏗️ Struktur Project

```
composeApp/src/
├── commonMain/kotlin/com/notes/
│   ├── data/repository/         # NoteRepository, NoteRepositoryImpl
│   ├── di/                      # AppModule, KoinInit
│   ├── domain/model/            # Note
│   └── presentation/
│       ├── screen/              # NotesScreen, TestTags
│       └── viewmodel/           # NotesViewModel, NotesUiState
│
├── commonTest/kotlin/com/notes/
│   ├── data/                    # NoteRepositoryTest (7 cases)
│   ├── di/                      # KoinModuleTest (2 cases)
│   └── presentation/
│       ├── NotesFlowTest        # Flow tests Turbine (3 cases)
│       └── NotesViewModelTest   # ViewModel + MockK (6 cases)
│
└── androidInstrumentedTest/kotlin/com/notes/
    └── NotesScreenTest          # UI tests Compose (5 cases)
```

---

## 🧪 Daftar Test Cases

### 1. NoteRepositoryTest (commonTest) — 7 test cases
| # | Test Case | Deskripsi |
|---|---|---|
| 1 | `getAllNotes_emitsEmptyListInitially` | Verifikasi list kosong saat pertama kali |
| 2 | `insertNote_addsNoteAndListIsUpdated` | Insert note dan cek list terupdate |
| 3 | `deleteNote_removesNoteFromRepository` | Hapus note dan verifikasi hilang dari list |
| 4 | `updateNote_modifiesExistingNoteCorrectly` | Update note dan verifikasi perubahan |
| 5 | `getNoteById_returnsCorrectNoteById` | Cari note by ID dan verifikasi benar |
| 6 | `deleteAllNotes_removesAllNotes` | Hapus semua note sekaligus |
| 7 | `getNoteById_returnsNullForNonExistingId` | Cari ID tidak ada, harus return null |

### 2. NotesViewModelTest (commonTest) — 6 test cases
| # | Test Case | Deskripsi |
|---|---|---|
| 1 | `initial state emits Loading then Success` | State awal Loading lalu Success |
| 2 | `addNote calls repository insertNote` | Verifikasi MockK: insertNote dipanggil |
| 3 | `deleteNote calls repository with correct id` | Verifikasi MockK: deleteNote dipanggil |
| 4 | `when repository throws, state should be Error` | Error handling dari repository |
| 5 | `success state with empty list` | Success state dengan notes kosong |
| 6 | `deleteNote called only once per invocation` | Verifikasi tidak ada double call |

### 3. NotesFlowTest (commonTest) — 3 test cases
| # | Test Case | Deskripsi |
|---|---|---|
| 1 | `flow emits updated list on each note insertion` | Flow reaktif saat insert |
| 2 | `flow emits correct state after delete operation` | Flow reaktif saat delete |
| 3 | `flow emits updated note after update operation` | Flow reaktif saat update |

### 4. KoinModuleTest (commonTest) — 2 test cases
| # | Test Case | Deskripsi |
|---|---|---|
| 1 | `verify all koin modules can be loaded` | Semua module bisa diload tanpa error |
| 2 | `verify koin dependency graph is valid` | Graph dependency valid |

### 5. NotesScreenTest (androidInstrumentedTest) — 5 test cases
| # | Test Case | Deskripsi |
|---|---|---|
| 1 | `notesScreen_showsEmptyState_initially` | Empty state tampil saat pertama buka |
| 2 | `notesScreen_addNote_showsNoteInList` | Note baru muncul di list setelah ditambah |
| 3 | `notesScreen_addButton_isDisplayedAndEnabled` | Tombol + tampil dan bisa diklik |
| 4 | `notesScreen_titleInput_acceptsTextInput` | Input judul menerima teks |
| 5 | `notesScreen_multipleNotes_displayedInList` | Multiple notes tampil di list |

---

## 🔧 Dependency Injection (Koin)

```kotlin
// dataModule — NoteRepository sebagai singleton
val dataModule = module {
    single<NoteRepository> { NoteRepositoryImpl() }
}

// viewModelModule — NotesViewModel dengan DI
val viewModelModule = module {
    viewModel { NotesViewModel(get()) }
}

val appModules = listOf(dataModule, viewModelModule)
```

---

## ▶️ Cara Menjalankan Test

**Unit Test:**
```bash
.\gradlew :composeApp:testDebugUnitTest
```

**UI Test (butuh emulator menyala):**
```bash
.\gradlew :composeApp:connectedDebugAndroidTest
```

---

## 📊 Hasil Test

### Unit Test
```
BUILD SUCCESSFUL
16 tests completed, 16 passed
```

## 🛠️ Tech Stack

- **Kotlin Multiplatform (KMP)**
- **Jetpack Compose** — UI
- **Koin** — Dependency Injection
- **kotlin.test** — Unit Testing
- **MockK** — Mocking library
- **Turbine** — Flow testing
- **Compose UI Test** — UI/Instrumented testing
- **Kover** — Code coverage
