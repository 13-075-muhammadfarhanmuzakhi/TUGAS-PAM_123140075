package com.example.tugas1_123140075

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform