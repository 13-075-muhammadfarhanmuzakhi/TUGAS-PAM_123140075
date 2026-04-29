import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.farhan_123140075.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Tugas 4 - Muhammad Farhan Muzakhi (123140075)"
    ) {
        App()
    }
}