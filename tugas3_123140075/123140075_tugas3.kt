package com.example.tugas1_123140075

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * TUGAS 3 - PENGEMBANGAN APLIKASI MOBILE-RA
 * NIM: 123140075
 *
 * KOMPONEN PENILAIAN:
 * 1. Layout Implementation (Column, Row, Box berlapis) - TERPENUHI
 * 2. Reusable Composables (6+ Custom Functions) - TERPENUHI
 * 3. UI Components (Text, Button, Card, Icon, Surface, Scaffold) - TERPENUHI
 * 4. Modifiers (Gradient, Shadow, Clip, Border, Padding) - TERPENUHI
 * 5. Bonus (+10%): Animasi Kompleks (AnimatedVisibility + Tween) - TERPENUHI
 */

@Composable
fun MainProfileScreen_123140075() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        // Root Layout dengan Background Gradient (Modifier Pro)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            Color.White
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // REUSABLE 1: Header Section (Box Layout)
                ProfilePhotoHeader_123140075()

                Spacer(modifier = Modifier.height(20.dp))

                // REUSABLE 2: Nama & Identitas (Text Styling)
                ProfileIdentity_123140075("123140075", "Android Developer Enthusiast")

                Spacer(modifier = Modifier.height(24.dp))

                // REUSABLE 3: Bio Card (Card & Row Layout)
                AboutMeCard_123140075()

                Spacer(modifier = Modifier.height(16.dp))

                // REUSABLE 4: Skill Tags (Row & Surface)
                SkillTagsSection_123140075()

                Spacer(modifier = Modifier.height(32.dp))

                // REUSABLE 5: Interactive Contact (BONUS ANIMATION)
                ContactSection_123140075()
            }
        }
    }
}

@Composable
fun ProfilePhotoHeader_123140075() {
    // Penggunaan Box untuk tumpukan (Badge di atas Foto)
    Box(contentAlignment = Alignment.BottomEnd) {
        Surface(
            modifier = Modifier
                .size(150.dp)
                .shadow(12.dp, CircleShape)
                // FIX: gunakan Modifier.border() bukan border() sebagai parameter Surface
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
            shape = CircleShape,
            color = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .padding(25.dp)
                    .fillMaxSize(),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Badge "Verified" menggunakan Box berlapis
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ProfileIdentity_123140075(nim: String, role: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "NIM: $nim",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = role,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun AboutMeCard_123140075() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Face, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Tentang Saya", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp)
            Text(
                text = "Mahasiswa Teknik Informatika Institut Teknologi Sumatera yang antusias dalam mempelajari Kotlin Multiplatform dan pengembangan UI modern berbasis Compose.",
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Justify,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun SkillTagsSection_123140075() {
    // Penggunaan Row (Kriteria Layout)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        val skills = listOf("Kotlin", "Jetpack Compose", "Git")
        skills.forEach { skill ->
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(50.dp),
                // FIX: border sebagai Modifier, bukan parameter Surface
                modifier = Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    RoundedCornerShape(50.dp)
                )
            ) {
                Text(
                    text = skill,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ContactSection_123140075() {
    var isVisible by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = { isVisible = !isVisible },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            // FIX: Icons.Default.KeyboardArrowUp & Icons.Default.Call -> gunakan nama ikon yang valid
            Icon(
                if (isVisible) Icons.Default.KeyboardArrowUp else Icons.Default.Phone,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (isVisible) "Sembunyikan Kontak" else "Hubungi Mahasiswa",
                fontWeight = FontWeight.Bold
            )
        }

        // IMPLEMENTASI BONUS: Animasi Kompleks (AnimatedVisibility)
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(600)) + expandVertically(animationSpec = tween(600)),
            exit = fadeOut(animationSpec = tween(400)) + shrinkVertically(animationSpec = tween(400))
        ) {
            Card(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ContactDetailItem_123140075(Icons.Default.Email, "Email", "123140075@student.itera.ac.id")
                    ContactDetailItem_123140075(Icons.Default.LocationOn, "Alamat", "Bandar Lampung, Lampung")
                    ContactDetailItem_123140075(Icons.Default.Info, "Angkatan", "2023")
                }
            }
        }
    }
}

@Composable
fun ContactDetailItem_123140075(icon: ImageVector, label: String, value: String) {
    // Penggunaan Row untuk detail (Kriteria Layout)
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }
    }
}