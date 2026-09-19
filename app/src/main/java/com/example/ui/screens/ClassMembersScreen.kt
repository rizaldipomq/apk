package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.ClassProfile
import com.example.data.model.StudentMember
import com.example.ui.components.InstagramBrandIcon
import com.example.ui.util.ClassUiUtils

@Composable
fun ClassMembersScreen(
    profile: ClassProfile,
    students: List<StudentMember>,
    searchQuery: String,
    genderFilter: String,
    isAdmin: Boolean = false,
    onSearchChange: (String) -> Unit,
    onGenderFilterChange: (String) -> Unit,
    onAddStudent: (StudentMember) -> Unit,
    onUpdateStudent: (StudentMember) -> Unit,
    onDeleteStudent: (StudentMember) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Struktur Organisasi, 1: Semua Siswa
    var selectedStudentForDetail by remember { mutableStateOf<StudentMember?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (selectedSubTab == 1 && isAdmin) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_student_fab")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Tambah Siswa")
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Tabs
            TabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = { Text("Struktur Organisasi", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.AccountTree, contentDescription = null) }
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = { Text("Daftar Siswa (${students.size})", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.People, contentDescription = null) }
                )
            }

            if (selectedSubTab == 0) {
                // --- TAB 1: STRUKTUR ORGANISASI KELAS ---
                OrganizationStructureView(
                    profile = profile,
                    students = students,
                    onSelectStudent = { selectedStudentForDetail = it }
                )
            } else {
                // --- TAB 2: DAFTAR SEMUA SISWA & PENCARIAN ---
                AllStudentsListView(
                    students = students,
                    searchQuery = searchQuery,
                    genderFilter = genderFilter,
                    onSearchChange = onSearchChange,
                    onGenderFilterChange = onGenderFilterChange,
                    onSelectStudent = { selectedStudentForDetail = it }
                )
            }
        }
    }

    // Detail Dialog
    selectedStudentForDetail?.let { student ->
        StudentDetailDialog(
            student = student,
            isAdmin = isAdmin,
            onDismiss = { selectedStudentForDetail = null },
            onDelete = {
                onDeleteStudent(student)
                selectedStudentForDetail = null
            }
        )
    }

    // Add Student Dialog
    if (showAddDialog) {
        AddStudentDialog(
            nextAbsent = (students.maxOfOrNull { it.absentNumber } ?: 0) + 1,
            onDismiss = { showAddDialog = false },
            onConfirm = { newStudent ->
                onAddStudent(newStudent)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun OrganizationStructureView(
    profile: ClassProfile,
    students: List<StudentMember>,
    onSelectStudent: (StudentMember) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("org_structure_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Wali Kelas
        item {
            OrgRoleHeader(title = "Wali Kelas & Pembina", icon = Icons.Default.CastForEducation)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "WALI KELAS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = profile.homeroomTeacher,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = profile.homeroomTeacherSubject,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Pimpinan Kelas (Ketua & Wakil)
        item {
            OrgRoleHeader(title = "Pimpinan Kelas", icon = Icons.Default.MilitaryTech)
            val leader = students.find { it.role.contains("Ketua", ignoreCase = true) && !it.role.contains("Wakil", ignoreCase = true) }
            val viceLeader = students.find { it.role.contains("Wakil", ignoreCase = true) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                leader?.let {
                    OrgMemberCard(
                        student = it,
                        roleTitle = "Ketua Kelas",
                        badgeColor = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectStudent(it) }
                    )
                }
                viceLeader?.let {
                    OrgMemberCard(
                        student = it,
                        roleTitle = "Wakil Ketua",
                        badgeColor = Color(0xFF0D9488),
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectStudent(it) }
                    )
                }
            }
        }

        // Sekretaris (1 & 2)
        item {
            OrgRoleHeader(title = "Sekretariat & Administrasi", icon = Icons.Default.EditNote)
            val secretaries = students.filter { it.role.contains("Sekretaris", ignoreCase = true) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                secretaries.forEach { sec ->
                    OrgMemberCard(
                        student = sec,
                        roleTitle = sec.role,
                        badgeColor = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectStudent(sec) }
                    )
                }
            }
        }

        // Bendahara (1 & 2)
        item {
            OrgRoleHeader(title = "Keuangan & Kas Kelas", icon = Icons.Default.Savings)
            val treasurers = students.filter { it.role.contains("Bendahara", ignoreCase = true) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                treasurers.forEach { tr ->
                    OrgMemberCard(
                        student = tr,
                        roleTitle = tr.role,
                        badgeColor = Color(0xFF10B981),
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectStudent(tr) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrgRoleHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun OrgMemberCard(
    student: StudentMember,
    roleTitle: String,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = badgeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = roleTitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(ClassUiUtils.getAvatarColor(student.avatarColorIndex), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ClassUiUtils.getInitials(student.name),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = student.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Absen ${student.absentNumber} • \"${student.nickname}\"",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AllStudentsListView(
    students: List<StudentMember>,
    searchQuery: String,
    genderFilter: String,
    onSearchChange: (String) -> Unit,
    onGenderFilterChange: (String) -> Unit,
    onSelectStudent: (StudentMember) -> Unit
) {
    val filteredStudents = remember(students, searchQuery, genderFilter) {
        students.filter { student ->
            val matchesSearch = searchQuery.isBlank() ||
                student.name.contains(searchQuery, ignoreCase = true) ||
                student.nickname.contains(searchQuery, ignoreCase = true) ||
                student.absentNumber.toString().contains(searchQuery) ||
                student.nisn.contains(searchQuery)

            val matchesGender = when (genderFilter) {
                "L" -> student.gender.equals("L", ignoreCase = true)
                "P" -> student.gender.equals("P", ignoreCase = true)
                else -> true
            }

            matchesSearch && matchesGender
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("all_students_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Cari nama, panggilan, atau no. absen...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_search_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Gender Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = genderFilter == "ALL",
                    onClick = { onGenderFilterChange("ALL") },
                    label = { Text("Semua (${students.size})") },
                    leadingIcon = {
                        if (genderFilter == "ALL") Icon(Icons.Default.Check, contentDescription = null)
                    }
                )
                FilterChip(
                    selected = genderFilter == "L",
                    onClick = { onGenderFilterChange("L") },
                    label = { Text("Laki-laki (${students.count { it.gender.equals("L", true) }})") },
                    leadingIcon = {
                        if (genderFilter == "L") Icon(Icons.Default.Check, contentDescription = null)
                    }
                )
                FilterChip(
                    selected = genderFilter == "P",
                    onClick = { onGenderFilterChange("P") },
                    label = { Text("Perempuan (${students.count { it.gender.equals("P", true) }})") },
                    leadingIcon = {
                        if (genderFilter == "P") Icon(Icons.Default.Check, contentDescription = null)
                    }
                )
            }
        }

        if (filteredStudents.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tidak ada data siswa yang cocok.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        } else {
            items(filteredStudents, key = { it.id }) { student ->
                StudentListItemCard(student = student, onClick = { onSelectStudent(student) })
            }
        }
    }
}

@Composable
private fun StudentListItemCard(
    student: StudentMember,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("student_item_${student.absentNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Absent Badge + Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = student.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (student.gender.equals("L", true)) "♂" else "♀",
                        color = if (student.gender.equals("L", true)) Color(0xFF2563EB) else Color(0xFFEC4899),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "Panggilan: \"${student.nickname}\" • ${if (student.role != "Anggota") student.role else "Siswa"}",
                    fontSize = 12.sp,
                    color = if (student.role != "Anggota") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (student.role != "Anggota") FontWeight.SemiBold else FontWeight.Normal
                )

                if (student.quote.isNotBlank()) {
                    Text(
                        text = "“${student.quote}”",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Detail",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun StudentDetailDialog(
    student: StudentMember,
    isAdmin: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Avatar with Photo or Number
                Box(
                    modifier = Modifier
                        .size(80.dp)
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
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "No. Absen ${student.absentNumber} • ${student.role}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detail info items
                DetailInfoRow(label = "Nama Panggilan", value = student.nickname, icon = Icons.Default.Badge)
                DetailInfoRow(label = "Jenis Kelamin", value = if (student.gender.equals("L", true)) "Laki-laki" else "Perempuan", icon = Icons.Default.Person)
                DetailInfoRow(label = "NISN", value = student.nisn.ifBlank { "-" }, icon = Icons.Default.Pin)
                DetailInfoRow(label = "Hobi", value = student.hobby.ifBlank { "-" }, icon = Icons.Default.SportsBasketball)
                DetailInfoRow(label = "Cita-cita", value = student.dream.ifBlank { "-" }, icon = Icons.Default.Stars)

                // Instagram with Brand Icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InstagramBrandIcon(size = 18.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Instagram: ",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = student.instagram.ifBlank { "-" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (student.instagram.isNotBlank()) Color(0xFF0284C7) else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (student.quote.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "“${student.quote}”",
                            modifier = Modifier.padding(10.dp),
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isAdmin) Arrangement.SpaceBetween else Arrangement.End
                ) {
                    if (isAdmin) {
                        TextButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hapus Siswa")
                        }
                    }

                    Button(onClick = onDismiss) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AddStudentDialog(
    nextAbsent: Int,
    onDismiss: () -> Unit,
    onConfirm: (StudentMember) -> Unit
) {
    var absentNumber by remember { mutableStateOf(nextAbsent.toString()) }
    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("L") }
    var role by remember { mutableStateOf("Anggota") }
    var nisn by remember { mutableStateOf("") }
    var quote by remember { mutableStateOf("") }
    var hobby by remember { mutableStateOf("") }
    var dream by remember { mutableStateOf("") }
    var instagram by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Tambah Anggota Kelas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = absentNumber,
                            onValueChange = { absentNumber = it },
                            label = { Text("No. Absen") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = { nickname = it },
                            label = { Text("Panggilan") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Lengkap Siswa *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Text("Jenis Kelamin:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = gender == "L",
                            onClick = { gender = "L" },
                            label = { Text("Laki-laki (L)") }
                        )
                        FilterChip(
                            selected = gender == "P",
                            onClick = { gender = "P" },
                            label = { Text("Perempuan (P)") }
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Jabatan / Peran (Anggota/Sie...)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = quote,
                        onValueChange = { quote = it },
                        label = { Text("Quotes / Kata Mutiara") },
                        modifier = Modifier.fillMaxWidth()
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
                        value = instagram,
                        onValueChange = { instagram = it },
                        label = { Text("Instagram (cth: @username)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Batal")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val abs = absentNumber.toIntOrNull() ?: nextAbsent
                                    onConfirm(
                                        StudentMember(
                                            absentNumber = abs,
                                            name = name.trim(),
                                            nickname = nickname.ifBlank { name.split(" ").firstOrNull() ?: "" },
                                            gender = gender,
                                            role = role.ifBlank { "Anggota" },
                                            nisn = nisn,
                                            quote = quote,
                                            hobby = hobby,
                                            dream = dream,
                                            instagram = instagram,
                                            avatarColorIndex = abs % 8
                                        )
                                    )
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
