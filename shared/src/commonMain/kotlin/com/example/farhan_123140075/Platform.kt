package com.example.farhan_123140075

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform