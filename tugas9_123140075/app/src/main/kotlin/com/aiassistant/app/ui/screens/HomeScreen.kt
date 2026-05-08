package com.aiassistant.app.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiassistant.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(checkConnectivity(context)) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        while (true) {
            delay(3000)
            isOnline = checkConnectivity(context)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "pulse"
    )
    val rotate by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotate"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Banner
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -40 }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Indigo800, Violet500.copy(0.8f), Cyan400.copy(0.5f))
                        )
                    )
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Violet400.copy(0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AutoAwesome, null, tint = Cyan300, modifier = Modifier.size(28.dp).rotate(rotate))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("AI Assistant", fontWeight = FontWeight.Bold, fontSize = 26.sp, color = White)
                            Text("Powered by Gemini AI", fontSize = 13.sp, color = White.copy(0.7f))
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(10.dp).clip(CircleShape)
                                .background(if (isOnline) Emerald400 else Rose500)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (isOnline) "Online - Ready to chat" else "Offline - Check your connection",
                            color = White, fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Device Info Card
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { 60 }) {
            DeviceInfoCard()
        }

        Spacer(Modifier.height(12.dp))

        // Features Grid
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { 80 }) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Features", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FeatureCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Chat, title = "AI Chat",
                        desc = "Multi-turn conversation", color = Violet400)
                    FeatureCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Psychology, title = "Smart AI",
                        desc = "Gemini 1.5 Flash", color = Cyan400)
                }
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FeatureCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Security, title = "Secure",
                        desc = "Local API key storage", color = Emerald400)
                    FeatureCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Tune, title = "Customizable",
                        desc = "System prompt & theme", color = Amber400)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Stats Row
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(1200)) + slideInVertically(tween(1200)) { 100 }) {
            StatsRow()
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun DeviceInfoCard() {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.PhoneAndroid, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Text("Device Info", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(16.dp))
            DeviceInfoRow(Icons.Filled.Memory, "Device", "${Build.MANUFACTURER} ${Build.MODEL}")
            DeviceInfoRow(Icons.Filled.Android, "Android", "API ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})")
            DeviceInfoRow(Icons.Filled.Fingerprint, "Build", Build.DISPLAY.take(24))
            DeviceInfoRow(Icons.Filled.Architecture, "ABI", Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown")
        }
    }
}

@Composable
fun DeviceInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary.copy(0.7f), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, modifier = Modifier.width(80.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun FeatureCard(modifier: Modifier = Modifier, icon: ImageVector, title: String, desc: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 14.sp)
        }
    }
}

@Composable
fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatBadge(modifier = Modifier.weight(1f), value = "2.0", label = "Gemini Flash", color = Violet400)
        StatBadge(modifier = Modifier.weight(1f), value = "âˆž", label = "Messages", color = Cyan400)
        StatBadge(modifier = Modifier.weight(1f), value = "Free", label = "Tier", color = Emerald400)
    }
}

@Composable
fun StatBadge(modifier: Modifier = Modifier, value: String, label: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(0.12f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = color)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

fun checkConnectivity(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val cap = cm.getNetworkCapabilities(network) ?: return false
    return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}