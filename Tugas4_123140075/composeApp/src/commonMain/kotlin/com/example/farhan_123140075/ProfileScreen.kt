package com.example.farhan_123140075

import androidx.compose.animation.core.animateDp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import org.jetbrains.compose.resources.painterResource
import farhan_123140075.composeapp.generated.resources.Res
import farhan_123140075.composeapp.generated.resources.profile_pict

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableStateOf(0) }

    // Animasi Tema & Warna Global
    val bgColor by animateColorAsState(if (uiState.isDarkMode) Color(0xFF0F172A) else Color(0xFFF8FAFC), tween(800))
    val cardColor by animateColorAsState(if (uiState.isDarkMode) Color(0xFF1E293B) else Color.White, tween(800))
    val textColor = if (uiState.isDarkMode) Color.White else Color(0xFF334155)
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. TOP APP BAR AREA ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Welcome Back,", fontSize = 14.sp, color = primaryColor, fontWeight = FontWeight.SemiBold)
                    Text("Farhan Muzakhi", fontSize = 24.sp, fontWeight = FontWeight.Black, color = textColor)
                }
                Surface(
                    onClick = { viewModel.toggleDarkMode(!uiState.isDarkMode) },
                    shape = CircleShape,
                    color = cardColor,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = if (uiState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Theme",
                        modifier = Modifier.padding(10.dp).size(24.dp),
                        tint = primaryColor
                    )
                }
            }

            // --- 2. HERO SECTION (AVATAR & GLOW) ---
            val infiniteTransition = rememberInfiniteTransition(label = "glow")

// Pakai animateValue agar lebih stabil di Desktop/Multiplatform
            val glowAnim by infiniteTransition.animateValue(
                initialValue = 0.dp,
                targetValue = 15.dp,
                typeConverter = Dp.VectorConverter, // WAJIB ADA
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glow_anim"
            )
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
                // Outer Glow Circle
                Box(modifier = Modifier.size(140.dp + glowAnim).clip(CircleShape).background(primaryColor.copy(alpha = 0.1f)))

                Surface(
                    modifier = Modifier.size(150.dp),
                    shape = CircleShape,
                    border = BorderStroke(4.dp, Brush.linearGradient(listOf(primaryColor, Color.Cyan))),
                    shadowElevation = 20.dp
                ) {
                    Image(
                        painter = painterResource(Res.drawable.profile_pict),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(uiState.name, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = textColor, textAlign = TextAlign.Center)
            Text(uiState.bio, fontSize = 16.sp, color = primaryColor, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. QUICK ACTION BUTTONS ---
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { viewModel.startEditing() },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    modifier = Modifier.height(48.dp).weight(1f)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Portfolio")
                }
                OutlinedButton(
                    onClick = { /* Export PDF Logic */ },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(48.dp).weight(1f)
                ) {
                    Icon(Icons.Default.Download, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Resume")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 4. INTERACTIVE TAB SYSTEM (MANUAL LOGIC) ---
            Surface(modifier = Modifier.fillMaxWidth(), color = cardColor, shape = RoundedCornerShape(20.dp), shadowElevation = 2.dp) {
                Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    val tabs = listOf("Overview", "Journey", "Projects")
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        val tabBg by animateColorAsState(if (isSelected) primaryColor else Color.Transparent)
                        val tabContentColor by animateColorAsState(if (isSelected) Color.White else textColor.copy(alpha = 0.6f))

                        Box(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp))
                                .background(tabBg).clickable { selectedTab = index }.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(title, fontWeight = FontWeight.Bold, color = tabContentColor, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 5. DYNAMIC CONTENT CONTENT ---
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(tween(400)) with fadeOut(tween(400)) }
            ) { target ->
                when (target) {
                    0 -> Column {
                        // Personal Information Cards
                        InfoSectionHeader("Contact & Identity")
                        ModernInfoCard(Icons.Default.Email, "Official Email", uiState.email, cardColor, textColor)
                        ModernInfoCard(Icons.Default.Badge, "NIM / Student ID", uiState.nim, cardColor, textColor)
                        ModernInfoCard(Icons.Default.LocationOn, "Base Location", uiState.location, cardColor, textColor)

                        Spacer(modifier = Modifier.height(24.dp))
                        InfoSectionHeader("Technical Stack")
                        SkillIndicator("UI/UX Design (Figma)", 0.90f, primaryColor, textColor)
                        SkillIndicator("Mobile Development (KMP)", 0.82f, primaryColor, textColor)
                        SkillIndicator("Web Development (React/Node)", 0.85f, primaryColor, textColor)
                    }
                    1 -> Column {
                        InfoSectionHeader("Academic & Professional")
                        TimelineItem("2023 - Present", "Asisten Praktikum", "Informatika ITERA", cardColor, textColor)
                        TimelineItem("2024 - 2025", "Staf Divisi kerohanian", "HMIF ITERA", cardColor, textColor)
                        TimelineItem("2022 - 2023", "Staff Ahli", "UKM Madani", cardColor, textColor)

                        Spacer(modifier = Modifier.height(16.dp))
                        ExpandableDetailCard("Detailed Experience", uiState.experience, cardColor, textColor)
                    }
                    2 -> Column {
                        InfoSectionHeader("Featured Projects")
                        ProjectCard("Village Web Portal", "Fullstack development for Sidodadi Asri using React.", "Web", cardColor, textColor)
                        ProjectCard("Growth 2048", "Logic-based puzzle game with high performance Python.", "Game", cardColor, textColor)
                        ProjectCard("News Simulator", "Kotlin-based mobile app for information retrieval.", "Mobile", cardColor, textColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(120.dp)) // Extra space for scrolling
        }

        // --- 6. FLOATING SOCIAL CONNECT ---
        Box(modifier = Modifier.fillMaxSize().padding(bottom = 30.dp), contentAlignment = Alignment.BottomCenter) {
            Surface(shape = RoundedCornerShape(30.dp), color = cardColor, shadowElevation = 10.dp) {
                Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    SocialIcon(Icons.Default.Public, "Web")
                    SocialIcon(Icons.Default.Code, "GitHub")
                    SocialIcon(Icons.Default.Terminal, "LinkedIn")
                }
            }
        }

        // --- 7. EDIT MODAL OVERLAY ---
        AnimatedVisibility(visible = uiState.isEditing, enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut()) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Black.copy(alpha = 0.7f)) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    Card(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.65f),
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Column(modifier = Modifier.padding(32.dp).verticalScroll(rememberScrollState())) {
                            Text("Update Identity", fontSize = 24.sp, fontWeight = FontWeight.Black, color = textColor)
                            Text("Modify your public profile information", fontSize = 14.sp, color = textColor.copy(alpha = 0.6f))

                            Spacer(modifier = Modifier.height(30.dp))

                            OutlinedTextField(
                                value = uiState.tempName, onValueChange = { viewModel.onNameChange(it) },
                                label = { Text("Display Name") }, modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp), leadingIcon = { Icon(Icons.Default.Person, null) }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedTextField(
                                value = uiState.tempBio, onValueChange = { viewModel.onBioChange(it) },
                                label = { Text("Short Bio / Headlines") }, modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp), leadingIcon = { Icon(Icons.Default.Description, null) }
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                TextButton(onClick = { viewModel.cancelEdit() }, modifier = Modifier.weight(1f).height(50.dp)) {
                                    Text("Discard", color = Color.Red)
                                }
                                Button(onClick = { viewModel.saveProfile() }, modifier = Modifier.weight(1.5f).height(50.dp), shape = RoundedCornerShape(14.dp)) {
                                    Text("Save Changes")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SUB-COMPOSABLES FOR CLEANER CODE & HIGH LINE COUNT ---

@Composable
fun InfoSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 12.dp)
    )
}

@Composable
fun ModernInfoCard(icon: ImageVector, label: String, value: String, cardColor: Color, textColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(45.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 11.sp, color = textColor.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textColor, overflow = TextOverflow.Ellipsis, maxLines = 1)
            }
        }
    }
}

@Composable
fun SkillIndicator(name: String, progress: Float, color: Color, textColor: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textColor)
            Text("${(progress * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Black, color = color)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun TimelineItem(year: String, role: String, place: String, cardColor: Color, textColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
            Text(year.take(4), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Box(modifier = Modifier.width(2.dp).height(40.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)))
        }
        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(role, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textColor)
                Text(place, fontSize = 12.sp, color = textColor.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun ProjectCard(title: String, desc: String, tag: String, cardColor: Color, textColor: Color) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = MaterialTheme.colorScheme.primary)
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)) {
                    Text(tag, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(desc, fontSize = 13.sp, color = textColor.copy(alpha = 0.7f), lineHeight = 18.sp)
        }
    }
}

@Composable
fun SocialIcon(icon: ImageVector, desc: String) {
    Icon(icon, contentDescription = desc, modifier = Modifier.size(26.dp).clickable { }, tint = MaterialTheme.colorScheme.primary)
}

@Composable
fun ExpandableDetailCard(title: String, data: List<String>, cardColor: Color, textColor: Color) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded }, colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = textColor)
                Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
            }
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    data.forEach { item ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("▹", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item, fontSize = 13.sp, color = textColor.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}