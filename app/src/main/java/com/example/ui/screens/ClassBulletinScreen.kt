package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Announcement

@Composable
fun ClassBulletinScreen(
    announcements: List<Announcement>,
    isAdmin: Boolean = false,
    onToggleCompleted: (Announcement) -> Unit,
    onAddAnnouncement: (title: String, category: String, content: String, dueDate: String) -> Unit,
    onDeleteAnnouncement: (Announcement) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_announcement_fab")
                ) {
                    Icon(Icons.Default.PostAdd, contentDescription = "Buat Pengumuman")
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("announcement_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Mading & Papan Tugas Kelas",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Pantau tugas rumah, informasi ujian, dan agenda kelas agar tidak ada yang terlewat!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            if (announcements.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada pengumuman atau tugas baru.",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                items(announcements, key = { it.id }) { item ->
                    AnnouncementDetailCard(
                        announcement = item,
                        isAdmin = isAdmin,
                        onToggleCompleted = { onToggleCompleted(item) },
                        onDelete = { onDeleteAnnouncement(item) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddAnnouncementDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, category, content, dueDate ->
                onAddAnnouncement(title, category, content, dueDate)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AnnouncementDetailCard(
    announcement: Announcement,
    isAdmin: Boolean,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryColor = when (announcement.category) {
        "Tugas/PR" -> Color(0xFFEF4444)
        "Ujian" -> Color(0xFFF59E0B)
        "Kegiatan" -> Color(0xFF3B82F6)
        else -> Color(0xFF10B981)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (announcement.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (announcement.isCompleted) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = categoryColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = announcement.category,
                        color = categoryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (announcement.dueDate.isNotBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Batas: ${announcement.dueDate}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (isAdmin) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Hapus",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = announcement.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textDecoration = if (announcement.isCompleted) TextDecoration.LineThrough else null,
                color = if (announcement.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = announcement.content,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Oleh: ${announcement.author} • ${announcement.datePosted}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = announcement.isCompleted,
                        onCheckedChange = { onToggleCompleted() },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = if (announcement.isCompleted) "Selesai" else "Tandai Selesai",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (announcement.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AddAnnouncementDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, content: String, dueDate: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tugas/PR") }
    var content by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    val categories = listOf("Tugas/PR", "Pengumuman", "Ujian", "Kegiatan")

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
                    text = "Buat Mading / Tugas Baru",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Pengumuman / Tugas *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Kategori:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Isi Keterangan Lengkap *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Tenggat / Deadline (cth: 25 Sep 2026)") },
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
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onConfirm(title.trim(), category, content.trim(), dueDate.trim())
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank()
                    ) {
                        Text("Terbitkan")
                    }
                }
            }
        }
    }
}
