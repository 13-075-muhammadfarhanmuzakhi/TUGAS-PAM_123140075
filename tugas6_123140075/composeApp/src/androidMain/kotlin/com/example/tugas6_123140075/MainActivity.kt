package com.example.tugas6_123140075

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tugas6_123140075.screens.MainScreen
import com.example.tugas6_123140075.tema.NewsReaderAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            NewsReaderAppTheme {
                MainScreen()
            }
        }
    }
}