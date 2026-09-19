package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.components.InstagramBrandIcon
import com.example.ui.components.SpotifyBrandIcon
import com.example.ui.util.ClassUiUtils

@Composable
fun ClassAdminScreen(
    profile: ClassProfile,
    isAdmin: Boolean,
    isSyncing: Boolean,
    students: List<StudentMember>,
    schedules: List<ClassSchedule>,
    pickets: List<PicketSchedule>,
    transactions: List<CashTransaction>,
    galleryItems: List<GalleryItem>,
    announcements: List<Announcement>,
    spotifyTracks: List<SpotifyTrackEntity>,
    onLogin: (String, String) -> Boolean,
    onLogout: () -> Unit,
    onSyncFromCloud: () -> Unit,
    onPushToCloud: () -> Unit,
    onUpdateProfile: (ClassProfile) -> Unit,
    onAddStudent: (StudentMember) -> Unit,
    onUpdateStudent: (StudentMember) -> Unit,
    onDeleteStudent: (StudentMember) -> Unit,
    onAddSchedule: (ClassSchedule) -> Unit,
    onDeleteSchedule: (ClassSchedule) -> Unit,
    onUpdatePicket: (PicketSchedule) -> Unit,
    onAddTransaction: (title: String, amount: Long, type: String, category: String, notes: String) -> Unit,
    onDeleteTransaction: (CashTransaction) -> Unit,
    onAddGalleryItem: (title: String, category: String, description: String, imageUrl: String) -> Unit,
    onDeleteGalleryItem: (GalleryItem) -> Unit,
    onAddAnnouncement: (title: String, category: String, content: String, dueDate: String) -> Unit,
    onDeleteAnnouncement: (Announcement) -> Unit,
    onAddSpotifyTrack: (SpotifyTrackEntity) -> Unit,
    onDeleteSpotifyTrack: (SpotifyTrackEntity) -> Unit,
    onResetData: () -> Unit,
    onNavigateTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Login form state
    var inputUsername by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // Admin Tab: 0=Profil/Teks/Banner, 1=Siswa (22), 2=Jadwal&Piket, 3=Kas, 4=Galeri, 5=Mading, 6=Spotify
    var adminActiveTab by remember { mutableStateOf(0) }

    // Reset confirmation
    var showResetConfirmation by remember { mutableStateOf(false) }

    if (!isAdmin) {
        // --- VISITOR / LOGIN SCREEN ---
        // Does NOT reveal username and password!
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_login_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(MaterialTheme.colorScheme.primary, Color(0xFF0284C7))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockPerson,
                            contentDescription = "Admin Lock",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Portal Kelola Admin & Developer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Menu khusus pengurus untuk mengubah isi teks, foto, jadwal, dan konten aplikasi kelas secara real-time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Mode pengunjung indicator
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pengunjung hanya dapat melihat data. Masuk untuk akses edit penuh.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (loginError != null) {
                        Text(
                            text = loginError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedTextField(
                        value = inputUsername,
                        onValueChange = {
                            inputUsername = it
                            loginError = null
                        },
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_username_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = inputPassword,
                        onValueChange = {
                            inputPassword = it
                            loginError = null
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            val success = onLogin(inputUsername.trim(), inputPassword.trim())
                            if (!success) {
                                loginError = "Username atau password admin salah. Hubungi developer."
                            }
                        }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_password_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val success = onLogin(inputUsername.trim(), inputPassword.trim())
                            if (!success) {
                                loginError = "Username atau password admin salah. Hubungi developer."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("admin_login_submit_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Masuk Sebagai Admin", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else {
        // --- AUTHENTICATED ADMIN DASHBOARD ("Tampilan Ke-2") ---
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Admin Top Banner Bar
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Panel Admin & Developer",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Mode Real-Time Online",
                                    fontSize = 11.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Cloud Sync action button
                            IconButton(
                                onClick = onSyncFromCloud,
                                enabled = !isSyncing
                            ) {
                                if (isSyncing) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.CloudSync, contentDescription = "Sync Cloud", tint = MaterialTheme.colorScheme.primary)
                                }
                            }

                            // Logout Button
                            TextButton(
                                onClick = onLogout,
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Keluar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Admin Sub-tabs Bar (Scrollable)
            ScrollableTabRow(
                selectedTabIndex = adminActiveTab,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(selected = adminActiveTab == 0, onClick = { adminActiveTab = 0 }, text = { Text("Website & Banner") })
                Tab(selected = adminActiveTab == 1, onClick = { adminActiveTab = 1 }, text = { Text("Siswa (${students.size})") })
                Tab(selected = adminActiveTab == 2, onClick = { adminActiveTab = 2 }, text = { Text("Jadwal & Piket") })
                Tab(selected = adminActiveTab == 3, onClick = { adminActiveTab = 3 }, text = { Text("Kas (${transactions.size})") })
                Tab(selected = adminActiveTab == 4, onClick = { adminActiveTab = 4 }, text = { Text("Galeri (${galleryItems.size})") })
                Tab(selected = adminActiveTab == 5, onClick = { adminActiveTab = 5 }, text = { Text("Mading (${announcements.size})") })
                Tab(selected = adminActiveTab == 6, onClick = { adminActiveTab = 6 }, text = { Text("Spotify (${spotifyTracks.size})") })
                Tab(selected = adminActiveTab == 7, onClick = { adminActiveTab = 7 }, text = { Text("Reset Data") })
            }

            // Tab Content
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                when (adminActiveTab) {
                    0 -> AdminProfileEditSection(
                        profile = profile,
                        onUpdateProfile = onUpdateProfile,
                        onPushToCloud = onPushToCloud,
                        isSyncing = isSyncing
                    )
                    1 -> AdminStudentsSection(
                        students = students,
                        onAddStudent = onAddStudent,
                        onUpdateStudent = onUpdateStudent,
                        onDeleteStudent = onDeleteStudent
                    )
                    2 -> AdminScheduleSection(
                        schedules = schedules,
                        pickets = pickets,
                        onAddSchedule = onAddSchedule,
                        onDeleteSchedule = onDeleteSchedule,
                        onUpdatePicket = onUpdatePicket
                    )
                    3 -> AdminFinanceSection(
                        transactions = transactions,
                        onAddTransaction = onAddTransaction,
                        onDeleteTransaction = onDeleteTransaction
                    )
                    4 -> AdminGallerySection(
                        galleryItems = galleryItems,
                        onAddGalleryItem = onAddGalleryItem,
                        onDeleteGalleryItem = onDeleteGalleryItem
                    )
                    5 -> AdminBulletinSection(
                        announcements = announcements,
                        onAddAnnouncement = onAddAnnouncement,
                        onDeleteAnnouncement = onDeleteAnnouncement
                    )
                    6 -> AdminSpotifySection(
                        spotifyTracks = spotifyTracks,
                        onAddSpotifyTrack = onAddSpotifyTrack,
                        onDeleteSpotifyTrack = onDeleteSpotifyTrack
                    )
                    7 -> AdminResetSection(
                        onReset = { showResetConfirmation = true }
                    )
                }
            }
        }
    }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Reset Data Website?") },
            text = { Text("Tindakan ini akan mengembalikan nama kelas, 22 siswa, jadwal pelajaran TKJ, kas, dan galeri ke data bawaan.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetData()
                        showResetConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya, Reset Semua")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirmation = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

// =========================================================================
// TAB 0: EDIT PROFILE / TEXT / BANNER (Ganti Semua Isi Website)
// =========================================================================
@Composable
private fun AdminProfileEditSection(
    profile: ClassProfile,
    onUpdateProfile: (ClassProfile) -> Unit,
    onPushToCloud: () -> Unit,
    isSyncing: Boolean
) {
    var className by remember(profile) { mutableStateOf(profile.className) }
    var schoolName by remember(profile) { mutableStateOf(profile.schoolName) }
    var academicYear by remember(profile) { mutableStateOf(profile.academicYear) }
    var slogan by remember(profile) { mutableStateOf(profile.slogan) }
    var homeroomTeacher by remember(profile) { mutableStateOf(profile.homeroomTeacher) }
    var homeroomSubject by remember(profile) { mutableStateOf(profile.homeroomTeacherSubject) }
    var classLeader by remember(profile) { mutableStateOf(profile.classLeader) }
    var welcomeMessage by remember(profile) { mutableStateOf(profile.welcomeMessage) }
    var instagram by remember(profile) { mutableStateOf(profile.instagramHandle) }
    var heroImageUrl by remember(profile) { mutableStateOf(profile.heroImageUrl) }
    var weeklyCashTarget by remember(profile) { mutableStateOf(profile.weeklyCashTarget.toString()) }

    var saveMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Setiap tombol Simpan ditekan, seluruh data website langsung terupdate dan tersinkron ke semua perangkat.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        item {
            Text("Identitas & Judul Header Website", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        item {
            OutlinedTextField(
                value = className,
                onValueChange = { className = it },
                label = { Text("Nama Kelas / Judul Utama *") },
                supportingText = { Text("Contoh: KELAS XI-TKJ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = schoolName,
                onValueChange = { schoolName = it },
                label = { Text("Nama Jurusan / Subtitle *") },
                supportingText = { Text("Contoh: TEKNIK KOMPUTER & JARINGAN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = slogan,
                onValueChange = { slogan = it },
                label = { Text("Slogan / Motto Kelas") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = academicYear,
                onValueChange = { academicYear = it },
                label = { Text("Tahun Ajaran") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Foto Utama / Banner Header Web", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        item {
            OutlinedTextField(
                value = heroImageUrl,
                onValueChange = { heroImageUrl = it },
                label = { Text("URL Foto Banner (Ganti Foto Utama)") },
                supportingText = { Text("Tempel URL foto (JPG/PNG). Kosongkan untuk gradient TKJ default.") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        if (heroImageUrl.isNotBlank()) {
            item {
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(140.dp)) {
                    AsyncImage(
                        model = heroImageUrl,
                        contentDescription = "Preview Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        item {
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Struktur Inti & Sosial Media", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = homeroomTeacher,
                    onValueChange = { homeroomTeacher = it },
                    label = { Text("Wali Kelas") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = homeroomSubject,
                    onValueChange = { homeroomSubject = it },
                    label = { Text("Guru Mapel") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        item {
            OutlinedTextField(
                value = classLeader,
                onValueChange = { classLeader = it },
                label = { Text("Ketua Kelas") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = instagram,
                onValueChange = { instagram = it },
                label = { Text("Instagram Kelas") },
                leadingIcon = { InstagramBrandIcon(size = 20.dp) },
                supportingText = { Text("Contoh: @tkj_sebelas.official") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = welcomeMessage,
                onValueChange = { welcomeMessage = it },
                label = { Text("Pesan Sambutan Beranda") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }

        item {
            OutlinedTextField(
                value = weeklyCashTarget,
                onValueChange = { weeklyCashTarget = it },
                label = { Text("Target Kas Mingguan (Rp)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            Button(
                onClick = {
                    val updated = profile.copy(
                        className = className.trim(),
                        schoolName = schoolName.trim(),
                        academicYear = academicYear.trim(),
                        slogan = slogan.trim(),
                        homeroomTeacher = homeroomTeacher.trim(),
                        homeroomTeacherSubject = homeroomSubject.trim(),
                        classLeader = classLeader.trim(),
                        welcomeMessage = welcomeMessage.trim(),
                        instagramHandle = instagram.trim(),
                        heroImageUrl = heroImageUrl.trim(),
                        weeklyCashTarget = weeklyCashTarget.toLongOrNull() ?: 5000L
                    )
                    onUpdateProfile(updated)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Perubahan & Sync ke Cloud", fontWeight = FontWeight.Bold)
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// =========================================================================
// TAB 1: KELOLA 22 SISWA & GANTI FOTO SISWA
// =========================================================================
@Composable
private fun AdminStudentsSection(
    students: List<StudentMember>,
    onAddStudent: (StudentMember) -> Unit,
    onUpdateStudent: (StudentMember) -> Unit,
    onDeleteStudent: (StudentMember) -> Unit
) {
    var studentToEdit by remember { mutableStateOf<StudentMember?>(null) }
    var showAddStudentDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total Siswa: ${students.size} Orang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Sentuh nama untuk edit foto, bio, atau hapus", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
            }

            Button(onClick = { showAddStudentDialog = true }) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tambah")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(students, key = { it.id }) { student ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { studentToEdit = student },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar or Photo
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(ClassUiUtils.getAvatarColor(student.avatarColorIndex)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (student.photoUrl.isNotBlank()) {
                                AsyncImage(
                                    model = student.photoUrl,
                                    contentDescription = student.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = "${student.absentNumber}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(student.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = "Absen ${student.absentNumber} • ${student.role} • ${student.gender}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { studentToEdit = student }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(onClick = { onDeleteStudent(student) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    // Edit Student Dialog
    studentToEdit?.let { student ->
        AdminEditStudentDialog(
            student = student,
            onDismiss = { studentToEdit = null },
            onConfirm = { updated ->
                onUpdateStudent(updated)
                studentToEdit = null
            }
        )
    }

    // Add Student Dialog
    if (showAddStudentDialog) {
        val nextAbsent = (students.maxOfOrNull { it.absentNumber } ?: 0) + 1
        AdminAddStudentDialog(
            nextAbsent = nextAbsent,
            onDismiss = { showAddStudentDialog = false },
            onConfirm = { newStudent ->
                onAddStudent(newStudent)
                showAddStudentDialog = false
            }
        )
    }
}

@Composable
private fun AdminEditStudentDialog(
    student: StudentMember,
    onDismiss: () -> Unit,
    onConfirm: (StudentMember) -> Unit
) {
    var name by remember { mutableStateOf(student.name) }
    var nickname by remember { mutableStateOf(student.nickname) }
    var absentNumber by remember { mutableStateOf(student.absentNumber.toString()) }
    var role by remember { mutableStateOf(student.role) }
    var gender by remember { mutableStateOf(student.gender) }
    var photoUrl by remember { mutableStateOf(student.photoUrl) }
    var instagram by remember { mutableStateOf(student.instagram) }
    var hobby by remember { mutableStateOf(student.hobby) }
    var dream by remember { mutableStateOf(student.dream) }
    var quote by remember { mutableStateOf(student.quote) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Edit Data & Foto Siswa", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Lengkap *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = { nickname = it },
                            label = { Text("Panggilan") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = absentNumber,
                            onValueChange = { absentNumber = it },
                            label = { Text("No. Absen") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Jabatan (Ketua/Wakil/Bendahara/dll)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = photoUrl,
                        onValueChange = { photoUrl = it },
                        label = { Text("URL Foto Siswa") },
                        supportingText = { Text("Masukkan tautan gambar (JPG/PNG)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                if (photoUrl.isNotBlank()) {
                    item {
                        Box(
                            modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = instagram,
                        onValueChange = { instagram = it },
                        label = { Text("Instagram (cth: @nama)") },
                        leadingIcon = { InstagramBrandIcon(size = 18.dp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = hobby,
                        onValueChange = { hobby = it },
                        label = { Text("Hobi") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = dream,
                        onValueChange = { dream = it },
                        label = { Text("Cita-cita") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = quote,
                        onValueChange = { quote = it },
                        label = { Text("Quotes / Kata Mutiara") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Batal") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    onConfirm(
                                        student.copy(
                                            name = name.trim(),
                                            nickname = nickname.trim(),
                                            absentNumber = absentNumber.toIntOrNull() ?: student.absentNumber,
                                            role = role.trim(),
                                            photoUrl = photoUrl.trim(),
                                            instagram = instagram.trim(),
                                            hobby = hobby.trim(),
                                            dream = dream.trim(),
                                            quote = quote.trim()
                                        )
                                    )
                                }
                            },
                            enabled = name.isNotBlank()
                        ) {
                            Text("Simpan Siswa")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAddStudentDialog(
    nextAbsent: Int,
    onDismiss: () -> Unit,
    onConfirm: (StudentMember) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var absentNumber by remember { mutableStateOf(nextAbsent.toString()) }
    var role by remember { mutableStateOf("Anggota") }
    var gender by remember { mutableStateOf("L") }
    var photoUrl by remember { mutableStateOf("") }
    var instagram by remember { mutableStateOf("") }
    var hobby by remember { mutableStateOf("") }
    var dream by remember { mutableStateOf("") }
    var quote by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Tambah Siswa Baru", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Lengkap *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = { nickname = it },
                            label = { Text("Panggilan") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = absentNumber,
                            onValueChange = { absentNumber = it },
                            label = { Text("No. Absen") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = gender == "L",
                            onClick = { gender = "L" },
                            label = { Text("Laki-laki") }
                        )
                        FilterChip(
                            selected = gender == "P",
                            onClick = { gender = "P" },
                            label = { Text("Perempuan") }
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Jabatan") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = photoUrl,
                        onValueChange = { photoUrl = it },
                        label = { Text("URL Foto Siswa") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = instagram,
                        onValueChange = { instagram = it },
                        label = { Text("Instagram (@username)") },
                        leadingIcon = { InstagramBrandIcon(size = 18.dp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Batal") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val newStudent = StudentMember(
                                        absentNumber = absentNumber.toIntOrNull() ?: nextAbsent,
                                        name = name.trim(),
                                        nickname = nickname.trim().ifBlank { name.split(" ").firstOrNull() ?: "" },
                                        gender = gender,
                                        role = role.trim().ifBlank { "Anggota" },
                                        hobby = hobby.trim(),
                                        dream = dream.trim(),
                                        instagram = instagram.trim(),
                                        quote = quote.trim(),
                                        avatarColorIndex = (absentNumber.toIntOrNull() ?: 1) % 6,
                                        photoUrl = photoUrl.trim()
                                    )
                                    onConfirm(newStudent)
                                }
                            },
                            enabled = name.isNotBlank()
                        ) {
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 2: KELOLA JADWAL & PIKET
// =========================================================================
@Composable
private fun AdminScheduleSection(
    schedules: List<ClassSchedule>,
    pickets: List<PicketSchedule>,
    onAddSchedule: (ClassSchedule) -> Unit,
    onDeleteSchedule: (ClassSchedule) -> Unit,
    onUpdatePicket: (PicketSchedule) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var picketToEdit by remember { mutableStateOf<PicketSchedule?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Jadwal Pelajaran TKJ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Button(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah Jadwal")
                }
            }
        }

        items(schedules, key = { it.id }) { schedule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${schedule.dayOfWeek} • Jam ke-${schedule.orderNumber} (${schedule.timeRange})", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(schedule.subject, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${schedule.teacher} • ${schedule.room}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { onDeleteSchedule(schedule) }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Regu Piket Kelas (Senin - Jumat)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        items(pickets, key = { it.id }) { picket ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Hari ${picket.dayOfWeek}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                        IconButton(onClick = { picketToEdit = picket }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Anggota", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Text("Anggota: ${picket.members}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (showAddDialog) {
        var day by remember { mutableStateOf("Senin") }
        var subject by remember { mutableStateOf("") }
        var teacher by remember { mutableStateOf("") }
        var room by remember { mutableStateOf("Lab TKJ") }
        var timeRange by remember { mutableStateOf("07:30 - 09:00") }
        var orderNumber by remember { mutableStateOf("1") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Tambah Jadwal Baru", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = day, onValueChange = { day = it }, label = { Text("Hari") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Mata Pelajaran *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Guru Pengampu") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = timeRange, onValueChange = { timeRange = it }, label = { Text("Jam Pelajaran") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Ruangan/Lab") }, modifier = Modifier.fillMaxWidth())

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
                        Button(
                            onClick = {
                                if (subject.isNotBlank()) {
                                    onAddSchedule(
                                        ClassSchedule(
                                            dayOfWeek = day.trim(),
                                            orderNumber = orderNumber.toIntOrNull() ?: 1,
                                            timeRange = timeRange.trim(),
                                            subject = subject.trim(),
                                            teacher = teacher.trim(),
                                            room = room.trim()
                                        )
                                    )
                                    showAddDialog = false
                                }
                            },
                            enabled = subject.isNotBlank()
                        ) {
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    }

    picketToEdit?.let { picket ->
        var membersText by remember { mutableStateOf(picket.members) }
        Dialog(onDismissRequest = { picketToEdit = null }) {
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Edit Anggota Piket ${picket.dayOfWeek}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = membersText,
                        onValueChange = { membersText = it },
                        label = { Text("Daftar Anggota (pisahkan koma)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { picketToEdit = null }) { Text("Batal") }
                        Button(onClick = {
                            onUpdatePicket(picket.copy(members = membersText.trim()))
                            picketToEdit = null
                        }) {
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 3: KELOLA KAS
// =========================================================================
@Composable
private fun AdminFinanceSection(
    transactions: List<CashTransaction>,
    onAddTransaction: (title: String, amount: Long, type: String, category: String, notes: String) -> Unit,
    onDeleteTransaction: (CashTransaction) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Catatan Kas Kelas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Catat Transaksi")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(transactions, key = { it.id }) { tx ->
                val isIncome = tx.type.equals("INCOME", true)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(tx.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${tx.date} • ${tx.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = "${if (isIncome) "+" else "-"} ${ClassUiUtils.formatRupiah(tx.amount)}",
                            fontWeight = FontWeight.Bold,
                            color = if (isIncome) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontSize = 13.sp
                        )
                        IconButton(onClick = { onDeleteTransaction(tx) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("INCOME") }
        var category by remember { mutableStateOf("Iuran") }
        var notes by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Catat Kas Baru", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = type == "INCOME", onClick = { type = "INCOME" }, label = { Text("Pemasukan") })
                        FilterChip(selected = type == "EXPENSE", onClick = { type = "EXPENSE" }, label = { Text("Pengeluaran") })
                    }

                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Keterangan *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Jumlah (Rp) *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
                        Button(
                            onClick = {
                                val amountVal = amount.toLongOrNull() ?: 0L
                                if (title.isNotBlank() && amountVal > 0) {
                                    onAddTransaction(title.trim(), amountVal, type, category.trim(), notes.trim())
                                    showAddDialog = false
                                }
                            },
                            enabled = title.isNotBlank() && (amount.toLongOrNull() ?: 0L) > 0
                        ) {
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 4: KELOLA GALERI FOTO
// =========================================================================
@Composable
private fun AdminGallerySection(
    galleryItems: List<GalleryItem>,
    onAddGalleryItem: (title: String, category: String, description: String, imageUrl: String) -> Unit,
    onDeleteGalleryItem: (GalleryItem) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Galeri Foto Kenangan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah Foto")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(galleryItems, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (item.imageUrl.isNotBlank()) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${item.category} • ${item.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onDeleteGalleryItem(item) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Kegiatan") }
        var description by remember { mutableStateOf("") }
        var imageUrl by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Tambah Foto Kenangan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Foto *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori (Kegiatan/Study Tour/dll)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL Foto (JPG/PNG)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi Cerita *") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
                        Button(
                            onClick = {
                                if (title.isNotBlank() && description.isNotBlank()) {
                                    onAddGalleryItem(title.trim(), category.trim(), description.trim(), imageUrl.trim())
                                    showAddDialog = false
                                }
                            },
                            enabled = title.isNotBlank() && description.isNotBlank()
                        ) {
                            Text("Simpan Foto")
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 5: KELOLA MADING & PENGUMUMAN
// =========================================================================
@Composable
private fun AdminBulletinSection(
    announcements: List<Announcement>,
    onAddAnnouncement: (title: String, category: String, content: String, dueDate: String) -> Unit,
    onDeleteAnnouncement: (Announcement) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Papan Mading & Agenda", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Buat Pengumuman")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(announcements, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${item.category} • ${item.datePosted} ${if (item.dueDate.isNotBlank()) "• Batas: ${item.dueDate}" else ""}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onDeleteAnnouncement(item) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Tugas/PR") }
        var content by remember { mutableStateOf("") }
        var dueDate by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Buat Agenda / Pengumuman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori (Tugas/PR / Ujian / Info)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Tenggat / Batas Tanggal (Opsional)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Isi Keterangan *") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
                        Button(
                            onClick = {
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    onAddAnnouncement(title.trim(), category.trim(), content.trim(), dueDate.trim())
                                    showAddDialog = false
                                }
                            },
                            enabled = title.isNotBlank() && content.isNotBlank()
                        ) {
                            Text("Publikasikan")
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 6: KELOLA SPOTIFY PLAYLIST REAL-TIME
// =========================================================================
@Composable
private fun AdminSpotifySection(
    spotifyTracks: List<SpotifyTrackEntity>,
    onAddSpotifyTrack: (SpotifyTrackEntity) -> Unit,
    onDeleteSpotifyTrack: (SpotifyTrackEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SpotifyBrandIcon(size = 22.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Spotify Playlist Kelas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah Lagu")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(spotifyTracks, key = { it.id }) { track ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).background(Color(0xFF1DB954).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFF1DB954), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(track.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${track.artist} • ${track.durationText}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onDeleteSpotifyTrack(track) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var artist by remember { mutableStateOf("") }
        var duration by remember { mutableStateOf("3:30") }
        var previewUrl by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SpotifyBrandIcon(size = 20.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambah Lagu ke Playlist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Lagu *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = artist, onValueChange = { artist = it }, label = { Text("Penyanyi / Artis *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Durasi (cth: 3:45)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(
                        value = previewUrl,
                        onValueChange = { previewUrl = it },
                        label = { Text("Audio URL / Stream MP3 (Opsional)") },
                        supportingText = { Text("Kosongkan untuk nada ambient sintetis offline.") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
                        Button(
                            onClick = {
                                if (title.isNotBlank() && artist.isNotBlank()) {
                                    val newTrack = SpotifyTrackEntity(
                                        title = title.trim(),
                                        artist = artist.trim(),
                                        album = "Playlist XI-TKJ",
                                        durationText = duration.trim().ifBlank { "0:30 (Preview)" },
                                        audioUrl = previewUrl.trim()
                                    )
                                    onAddSpotifyTrack(newTrack)
                                    showAddDialog = false
                                }
                            },
                            enabled = title.isNotBlank() && artist.isNotBlank()
                        ) {
                            Text("Tambahkan")
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 7: RESET DATA
// =========================================================================
@Composable
private fun AdminResetSection(onReset: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.RestartAlt, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Reset Data ke Setelan Awal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Fitur ini akan mengembalikan data profil kelas TKJ, 22 siswa, jadwal pelajaran, piket, kas, dan galeri ke data template default.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Kembalikan ke Setelan Awal", fontWeight = FontWeight.Bold)
        }
    }
}
