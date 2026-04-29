package com.example.farhan_123140075

data class ProfileState(
    val name: String = "Muhammad Farhan Muzakhi",
    val nim: String = "123140075",
    val bio: String = "UI/UX Designer | Front-end Developer",
    val email: String = "muhammad.123140075@student.itera.ac.id",
    val phone: String = "082184054750",
    val location: String = "Institut Teknologi Sumatera",
    val isDarkMode: Boolean = false,
    val skills: List<String> = listOf("UI/UX", "Front-end", "KMP"),
    val experience: List<String> = listOf(
        "Asisten Praktikum Pengenalan Komputer Software 1 & 2",
        "Asisten Praktikum Dasar-Dasar Pemrograman (DTD)",
        "Asisten Tutorial Brain Boost-LTPB",
        "LKMM TD-Kemahasiswaan ITERA",
        "Ketua Pelaksana Informatech",
        "Ketua Pelaksana Maulid nabi-HMIF"
    ),
    val organizations: List<String> = listOf("HMIF", "KM-ITERA", "MADANI"),

    // Fitur Edit
    val isEditing: Boolean = false,
    val tempName: String = "",
    val tempBio: String = ""
)