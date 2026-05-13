# Notes App - Week 10: Koin DI + Testing

## Struktur Project

```
composeApp/src/
├── commonMain/kotlin/com/notes/
│   ├── data/repository/        # NoteRepository interface & impl
│   ├── di/                     # Koin modules (dataModule, viewModelModule)
│   ├── domain/model/           # Note data class
│   └── presentation/
│       ├── screen/             # NotesScreen composable + TestTags
│       └── viewmodel/          # NotesViewModel + NotesUiState
├── commonTest/kotlin/com/notes/
│   ├── data/NoteRepositoryTest.kt   # 6 repository tests
│   ├── di/KoinModuleTest.kt         # Koin verification tests
│   └── presentation/
│       ├── NotesViewModelTest.kt    # 5 ViewModel tests (MockK)
│       └── NotesFlowTest.kt         # 3 Flow tests (Turbine)
└── androidInstrumentedTest/
    └── NotesScreenTest.kt           # 5 UI tests (Compose Test)
```

## Daftar Test Cases

### NoteRepositoryTest (6 test cases)
| No | Test | Keterangan |
|----|------|-----------|
| 1 | getAllNotes emits empty list initially | Initial state |
| 2 | insertNote adds note and list is updated | Insert operation |
| 3 | deleteNote removes note from repository | Delete operation |
| 4 | updateNote modifies existing note correctly | Update operation |
| 5 | getNoteById returns correct note by id | Query by ID |
| 6 | deleteAllNotes removes all notes | Bulk delete |

### NotesViewModelTest (5 test cases dengan MockK)
| No | Test | Keterangan |
|----|------|-----------|
| 1 | initial state is Loading then Success | State flow |
| 2 | addNote calls repository insertNote | Repository call |
| 3 | addNote with blank title sets Error state | Validation |
| 4 | deleteNote calls repository with correct id | Delete verification |
| 5 | updateNote calls repository updateNote | Update verification |

### NotesFlowTest (3 test cases dengan Turbine)
| No | Test | Keterangan |
|----|------|-----------|
| 1 | flow emits updated list on each insertion | Insert flow |
| 2 | flow emits correct state after delete | Delete flow |
| 3 | flow emits updated note after update | Update flow |

### NotesScreenTest (5 UI test cases dengan Compose Test)
| No | Test | Keterangan |
|----|------|-----------|
| 1 | showsEmptyState initially | Empty state UI |
| 2 | addNote showsNoteInList | Add interaction |
| 3 | addButton isDisplayedAndEnabled | Button state |
| 4 | titleInput acceptsTextInput | Input interaction |
| 5 | multipleNotes displayedInList | Multiple items |

### KoinModuleTest
| No | Test | Keterangan |
|----|------|-----------|
| 1 | all koin modules can be loaded | Module loading |
| 2 | koin dependency graph is valid | checkModules() |

## Cara Menjalankan Tests

```bash
# Unit tests
./gradlew :composeApp:testDebugUnitTest

# UI tests (butuh emulator/device)
./gradlew :composeApp:connectedAndroidTest

# Coverage report (min 60%)
./gradlew :composeApp:koverHtmlReportDebug
# Output: composeApp/build/reports/kover/html/index.html
```

## Koin DI Setup

```kotlin
// 2 modules: dataModule + viewModelModule
val dataModule = module {
    single<NoteRepository> { NoteRepositoryImpl() }
}
val viewModelModule = module {
    viewModel { NotesViewModel(get()) }
}
```

---
*Tugas Praktikum Minggu 10 - Pengembangan Aplikasi Mobile - ITERA*
