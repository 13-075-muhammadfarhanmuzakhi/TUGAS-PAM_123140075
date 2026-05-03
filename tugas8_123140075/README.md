# NoteNest - Tugas 8
## Platform-Specific Features: expect/actual, Koin DI, Platform APIs

**Nama:** Muhammad Farhan Muzakhi  
**NIM:** 123140075  
**Kelas:** Pengembangan Aplikasi Mobile RA  
**Institut:** Institut Teknologi Sumatera

---

## Video Demo

[Link Google Drive - Video Demo](https://drive.google.com/drive/folders/1G4dB3LdbNk5XvtRSw_jJ-cwYt3hssG2J?hl=ID)

---

## Screenshot

### Tampilan Awal
![Tampilan Awal](screenshots/tampilan_awal.png)

### Device Info di Settings
![Device Info](screenshots/device_info.png)

### Indikator Offline
![Peringatan Offline](screenshots/peringatan_offline.png)

### Banner Offline di Main Screen
![DI Offline](screenshots/di_offline.png)

---

## Fitur yang Diimplementasikan

### 1. Koin Dependency Injection
Setup Koin di seluruh aplikasi menggunakan `KoinApplication` di `App.kt`. Semua dependency seperti repository, viewmodel, dan platform service di-inject lewat Koin.

```kotlin
val platformModule = module {
    single { DeviceInfo() }
    single { NetworkMonitor() }
    single { BatteryInfo() }
}
```

### 2. DeviceInfo (expect/actual)
Mengambil informasi perangkat seperti hostname, nama OS, versi OS, dan versi JVM menggunakan `System.getProperty()` dan `InetAddress`.

```kotlin
// commonMain
expect class DeviceInfo() {
    fun getDeviceName(): String
    fun getOsName(): String
    fun getOsVersion(): String
    fun getAppVersion(): String
    fun getJvmVersion(): String
}
```

### 3. NetworkMonitor (expect/actual)
Mengecek koneksi internet dengan cara mencoba socket ke `8.8.8.8:53`. Status koneksi di-observe lewat Flow dengan polling setiap 3 detik.

```kotlin
actual fun isConnected(): Boolean {
    return try {
        Socket().use { it.connect(InetSocketAddress("8.8.8.8", 53), 1500); true }
    } catch (e: Exception) { false }
}
```

### 4. NetworkStatusBanner
Banner merah animasi yang muncul otomatis di bagian atas MainScreen saat koneksi terputus.

### 5. BatteryInfo (expect/actual) - BONUS
Menampilkan status baterai. Pada desktop/AC power menampilkan `"Desktop/AC Power"`. Mendukung Linux dan Windows.

---

## Struktur File Baru

```
platform/
├── DeviceInfo.kt              (expect - commonMain)
├── NetworkMonitor.kt          (expect - commonMain)
├── BatteryInfo.kt             (expect - commonMain)
├── DeviceInfo.desktop.kt      (actual - desktopMain)
├── NetworkMonitor.desktop.kt  (actual - desktopMain)
└── BatteryInfo.desktop.kt     (actual - desktopMain)

di/
├── AppModule.kt               (Koin modules - commonMain)
└── DesktopModule.kt           (Koin desktop - desktopMain)

ui/components/
└── NetworkStatusBanner.kt
```

---

## Cara Menjalankan

Pastikan JAVA_HOME sudah di-set ke JBR dari Android Studio, lalu jalankan:

```
.\gradlew :composeApp:run --no-configuration-cache
```

---

## Referensi

- [Kotlin Multiplatform - expect/actual](https://kotlinlang.org/docs/multiplatform-connect-to-apis.html)
- [Koin Documentation](https://insert-koin.io/docs/quickstart/kotlin)
- Materi Pertemuan 8 - Platform-Specific Features
