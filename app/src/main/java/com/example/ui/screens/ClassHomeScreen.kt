package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.Announcement
import com.example.data.model.ClassProfile
import com.example.data.model.ClassSchedule
import com.example.data.model.SpotifyTrackEntity
import com.example.data.repository.CashSummary
import com.example.ui.components.InstagramBrandIcon
import com.example.ui.components.SpotifyBrandIcon
import com.example.ui.util.ClassUiUtils
import com.example.ui.util.SpotifyClassPlayer

@Composable
fun ClassHomeScreen(
    profile: ClassProfile,
    cashSummary: CashSummary,
    todaySchedules: List<ClassSchedule>,
    recentAnnouncements: List<Announcement>,
    spotifyTracks: List<SpotifyTrackEntity> = emptyList(),
    cloudStatus: String = "Terkoneksi",
    onNavigateTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(spotifyTracks) {
        if (spotifyTracks.isNotEmpty()) {
            SpotifyClassPlayer.updateFromEntities(spotifyTracks)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // --- 1. Hero Banner ---
        item {
            ClassHeroBanner(profile = profile)
        }

        // --- 2. Quick Class Stats ---
        item {
            ClassOverviewStats(
                profile = profile,
                cashSummary = cashSummary,
                onNavigateTab = onNavigateTab
            )
        }

        // --- 2.5 Spotify Real-time Music Player ---
        item {
            SpotifyPlayerWidget()
        }

        // --- 3. Quick Action Grid ---
        item {
            QuickActionShortcuts(onNavigateTab = onNavigateTab)
        }

        // --- 4. Today's Class Schedule Preview ---
        item {
            TodaySchedulePreview(
                schedules = todaySchedules,
                onViewFull = { onNavigateTab(2) }
            )
        }

        // --- 5. Homeroom Teacher & Class Leader Greeting ---
        item {
            ClassLeaderGreeting(profile = profile, onNavigateMembers = { onNavigateTab(1) })
        }

        // --- 6. Recent Bulletin & Homework ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mading & Pengumuman Terbaru",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigateTab(5) }) {
                    Text("Lihat Semua")
                }
            }
        }

        if (recentAnnouncements.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "Belum ada pengumuman kelas saat ini.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(recentAnnouncements.take(3)) { item ->
                AnnouncementHomeCard(item = item, onClick = { onNavigateTab(5) })
            }
        }
    }
}

@Composable
private fun ClassHeroBanner(profile: ClassProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
    ) {
        // Hero Image (supports custom URL or local asset)
        if (profile.heroImageUrl.isNotBlank()) {
            AsyncImage(
                model = profile.heroImageUrl,
                contentDescription = "Banner Kelas",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.img_class_hero)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.img_class_hero),
                contentDescription = "Banner Kelas",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Gradient Overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x660A192F),
                            Color(0xF00A192F)
                        )
                    )
                )
        )

        // Text & Badge Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Surface(
                color = Color(0xFF0284C7),
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${profile.schoolName} • TA ${profile.academicYear}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "KELAS XI-TKJ",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "TEKNIK KOMPUTER & JARINGAN",
                color = Color(0xFF38BDF8),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "“${profile.slogan}”",
                color = Color(0xFFE2E8F0),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                InstagramBrandIcon(size = 18.dp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = profile.instagramHandle,
                    color = Color(0xFF93C5FD),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ClassOverviewStats(
    profile: ClassProfile,
    cashSummary: CashSummary,
    onNavigateTab: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Total Siswa Card
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Groups,
            iconTint = Color(0xFF2563EB),
            iconBg = Color(0xFFDBEAFE),
            label = "Total Siswa",
            value = "${profile.totalStudents} Orang",
            subtitle = "${profile.maleStudents} L • ${profile.femaleStudents} P",
            onClick = { onNavigateTab(1) }
        )

        // Kas Kelas Card
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.AccountBalanceWallet,
            iconTint = Color(0xFF0D9488),
            iconBg = Color(0xFFCCFBF1),
            label = "Saldo Kas",
            value = ClassUiUtils.formatRupiah(cashSummary.currentBalance),
            subtitle = "${cashSummary.totalTransactions} transaksi",
            onClick = { onNavigateTab(3) }
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    value: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun QuickActionShortcuts(onNavigateTab: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Menu Cepat Portal Kelas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionButton(
                label = "Struktur",
                icon = Icons.Default.AccountTree,
                color = Color(0xFF3B82F6),
                onClick = { onNavigateTab(1) }
            )
            QuickActionButton(
                label = "Jadwal",
                icon = Icons.Default.CalendarToday,
                color = Color(0xFF8B5CF6),
                onClick = { onNavigateTab(2) }
            )
            QuickActionButton(
                label = "Kas Kelas",
                icon = Icons.Default.ReceiptLong,
                color = Color(0xFF10B981),
                onClick = { onNavigateTab(3) }
            )
            QuickActionButton(
                label = "Galeri",
                icon = Icons.Default.PhotoLibrary,
                color = Color(0xFFF59E0B),
                onClick = { onNavigateTab(4) }
            )
            QuickActionButton(
                label = "Kelola",
                icon = Icons.Default.AdminPanelSettings,
                color = Color(0xFFEC4899),
                onClick = { onNavigateTab(6) }
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TodaySchedulePreview(
    schedules: List<ClassSchedule>,
    onViewFull: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Jadwal Pelajaran Hari Ini",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = onViewFull) {
                    Text("Jadwal Lengkap")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (schedules.isEmpty()) {
                Text(
                    text = "Tidak ada jadwal pelajaran untuk hari ini (Hari libur).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                schedules.take(3).forEach { schedule ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = schedule.timeRange,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = schedule.subject,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${schedule.teacher} • ${schedule.room}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassLeaderGreeting(
    profile: ClassProfile,
    onNavigateMembers: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.secondary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Sambutan & Visi Kelas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "${profile.homeroomTeacher} (${profile.homeroomTeacherSubject})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "“${profile.welcomeMessage}”",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onNavigateMembers) {
                    Text(
                        text = "Lihat Profil Pengurus Kelas →",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AnnouncementHomeCard(
    item: Announcement,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val categoryColor = when (item.category) {
                "Tugas/PR" -> Color(0xFFEF4444)
                "Ujian" -> Color(0xFFF59E0B)
                "Kegiatan" -> Color(0xFF3B82F6)
                else -> Color(0xFF10B981)
            }

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(categoryColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                    if (item.dueDate.isNotBlank()) {
                        Text(
                            text = "Deadline: ${item.dueDate}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Text(
                    text = item.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.content,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SpotifyPlayerWidget() {
    val context = LocalContext.current
    val currentTrack by SpotifyClassPlayer.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by SpotifyClassPlayer.isPlaying.collectAsStateWithLifecycle()
    val playlist by SpotifyClassPlayer.playlist.collectAsStateWithLifecycle()

    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("spotify_player_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Spotify Logo / Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SpotifyBrandIcon(size = 24.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SPOTIFY CLASS PLAYLIST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1DB954),
                        letterSpacing = 1.2.sp
                    )
                }

                Surface(
                    color = if (isPlaying) Color(0xFF1DB954).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(if (isPlaying) Color(0xFF1DB954) else Color.Gray, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlaying) "MEMUTAR REAL-TIME" else "PAUSED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPlaying) Color(0xFF1DB954) else Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Track details with spinning cover
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF282828))
                        .then(if (isPlaying) Modifier.rotate(rotation) else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(currentTrack.coverUrl)
                            .crossfade(true)
                            .build(),
                        placeholder = rememberVectorPainter(image = Icons.Default.MusicNote),
                        error = rememberVectorPainter(image = Icons.Default.MusicNote),
                        contentDescription = "Cover Album",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Small vinyl hole
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(Color(0xFF121212), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentTrack.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${currentTrack.artist} • ${currentTrack.album}",
                        fontSize = 12.sp,
                        color = Color(0xFFB3B3B3),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { SpotifyClassPlayer.playPrevious(context) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Track",
                            tint = Color.White
                        )
                    }

                    FilledIconButton(
                        onClick = { SpotifyClassPlayer.togglePlay(context) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFF1DB954),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play"
                        )
                    }

                    IconButton(
                        onClick = { SpotifyClassPlayer.playNext(context) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Track",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Playlist Quick Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                playlist.forEach { track ->
                    val isSelected = track.id == currentTrack.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { SpotifyClassPlayer.playTrack(context, track) },
                        label = {
                            Text(
                                text = track.title,
                                fontSize = 11.sp,
                                maxLines = 1,
                                color = if (isSelected) Color.Black else Color(0xFFCCCCCC)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1DB954),
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFF282828)
                        ),
                        border = null
                    )
                }
            }
        }
    }
}

