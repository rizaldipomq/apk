package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ClassSchedule
import com.example.data.model.PicketSchedule
import com.example.ui.util.ClassUiUtils

@Composable
fun ClassScheduleScreen(
    schedules: List<ClassSchedule>,
    pickets: List<PicketSchedule>,
    selectedDay: String,
    isAdmin: Boolean = false,
    onSelectDay: (String) -> Unit,
    onAddSchedule: (ClassSchedule) -> Unit,
    onDeleteSchedule: (ClassSchedule) -> Unit,
    onUpdatePicket: (PicketSchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    var scheduleSubTab by remember { mutableStateOf(0) } // 0: Jadwal Pelajaran, 1: Jadwal Piket
    var showAddScheduleDialog by remember { mutableStateOf(false) }
    var picketToEdit by remember { mutableStateOf<PicketSchedule?>(null) }

    val daysOfWeek = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")

    Scaffold(
        floatingActionButton = {
            if (scheduleSubTab == 0 && isAdmin) {
                FloatingActionButton(
                    onClick = { showAddScheduleDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_schedule_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Jadwal")
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
            // Sub-Tabs: Pelajaran vs Piket
            TabRow(
                selectedTabIndex = scheduleSubTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = scheduleSubTab == 0,
                    onClick = { scheduleSubTab = 0 },
                    text = { Text("Jadwal Pelajaran", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null) }
                )
                Tab(
                    selected = scheduleSubTab == 1,
                    onClick = { scheduleSubTab = 1 },
                    text = { Text("Jadwal Piket Kelas", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.CleaningServices, contentDescription = null) }
                )
            }

            if (scheduleSubTab == 0) {
                // Day selector chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(daysOfWeek) { day ->
                        FilterChip(
                            selected = selectedDay == day,
                            onClick = { onSelectDay(day) },
                            label = { Text(day, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                if (selectedDay == day) Icon(Icons.Default.Check, contentDescription = null)
                            }
                        )
                    }
                }

                // Filtered schedules for selected day
                val daySchedules = remember(schedules, selectedDay) {
                    schedules.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }
                        .sortedBy { it.orderNumber }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("schedule_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (daySchedules.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada mata pelajaran untuk hari $selectedDay.",
                                    color = MaterialTheme.colorScheme.outline,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        items(daySchedules, key = { it.id }) { schedule ->
                            ScheduleCardItem(
                                schedule = schedule,
                                isAdmin = isAdmin,
                                onDelete = { onDeleteSchedule(schedule) }
                            )
                        }
                    }
                }
            } else {
                // Piket Schedules
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("picket_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Regu piket bertugas menyapu, mengepel, membersihkan papan tulis, dan menjaga kerapian ruang kelas.",
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    items(pickets, key = { it.id }) { picket ->
                        PicketScheduleCard(
                            picket = picket,
                            isAdmin = isAdmin,
                            onEdit = { picketToEdit = picket }
                        )
                    }
                }
            }
        }
    }

    if (showAddScheduleDialog) {
        AddScheduleDialog(
            defaultDay = selectedDay,
            onDismiss = { showAddScheduleDialog = false },
            onConfirm = { newSchedule ->
                onAddSchedule(newSchedule)
                showAddScheduleDialog = false
            }
        )
    }

    picketToEdit?.let { picket ->
        EditPicketDialog(
            picket = picket,
            onDismiss = { picketToEdit = null },
            onConfirm = { updatedPicket ->
                onUpdatePicket(updatedPicket)
                picketToEdit = null
            }
        )
    }
}

@Composable
private fun ScheduleCardItem(
    schedule: ClassSchedule,
    isAdmin: Boolean,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Order number pill
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${schedule.orderNumber}",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = schedule.timeRange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = schedule.subject,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${schedule.teacher} • ${schedule.room}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isAdmin) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Hapus Jadwal",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun PicketScheduleCard(
    picket: PicketSchedule,
    isAdmin: Boolean,
    onEdit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "HARI ${picket.dayOfWeek.uppercase()}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (isAdmin) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Regu Piket",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Anggota Piket:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Split and show members
            val membersList = picket.members.split(",").map { it.trim() }.filter { it.isNotBlank() }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                membersList.forEachIndexed { idx, member ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(ClassUiUtils.getAvatarColor(idx), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${idx + 1}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = member,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Tugas: ${picket.taskDescription}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun AddScheduleDialog(
    defaultDay: String,
    onDismiss: () -> Unit,
    onConfirm: (ClassSchedule) -> Unit
) {
    var day by remember { mutableStateOf(defaultDay) }
    var orderNumber by remember { mutableStateOf("1") }
    var subject by remember { mutableStateOf("") }
    var timeRange by remember { mutableStateOf("07.15 - 08.45") }
    var teacher by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("Ruang XII-1") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Tambah Jadwal Pelajaran",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Mata Pelajaran *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = day,
                        onValueChange = { day = it },
                        label = { Text("Hari") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = orderNumber,
                        onValueChange = { orderNumber = it },
                        label = { Text("Jam Ke-") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = timeRange,
                    onValueChange = { timeRange = it },
                    label = { Text("Rentang Jam (cth: 07.15 - 08.45)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Guru Pengampu") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Ruangan / Lab") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

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
                            if (subject.isNotBlank()) {
                                onConfirm(
                                    ClassSchedule(
                                        dayOfWeek = day,
                                        orderNumber = orderNumber.toIntOrNull() ?: 1,
                                        subject = subject.trim(),
                                        timeRange = timeRange,
                                        teacher = teacher.ifBlank { "Guru Mapel" },
                                        room = room.ifBlank { "Kelas" }
                                    )
                                )
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

@Composable
private fun EditPicketDialog(
    picket: PicketSchedule,
    onDismiss: () -> Unit,
    onConfirm: (PicketSchedule) -> Unit
) {
    var membersText by remember { mutableStateOf(picket.members) }
    var taskDesc by remember { mutableStateOf(picket.taskDescription) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Edit Regu Piket: Hari ${picket.dayOfWeek}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = membersText,
                    onValueChange = { membersText = it },
                    label = { Text("Nama Anggota (pisahkan dengan koma)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = taskDesc,
                    onValueChange = { taskDesc = it },
                    label = { Text("Deskripsi Tugas Kebersihan") },
                    modifier = Modifier.fillMaxWidth()
                )

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
                            onConfirm(
                                picket.copy(
                                    members = membersText,
                                    taskDescription = taskDesc
                                )
                            )
                        }
                    ) {
                        Text("Simpan Perubahan")
                    }
                }
            }
        }
    }
}
