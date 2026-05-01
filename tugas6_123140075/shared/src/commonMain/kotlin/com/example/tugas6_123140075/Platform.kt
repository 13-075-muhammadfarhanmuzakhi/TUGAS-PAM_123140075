package com.example.tugas6_123140075

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform