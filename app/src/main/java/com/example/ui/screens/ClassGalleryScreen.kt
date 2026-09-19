package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.GalleryItem
import com.example.ui.util.ClassUiUtils

@Composable
fun ClassGalleryScreen(
    galleryItems: List<GalleryItem>,
    selectedCategory: String,
    isAdmin: Boolean = false,
    onCategoryChange: (String) -> Unit,
    onToggleLike: (GalleryItem) -> Unit,
    onAddGalleryItem: (title: String, category: String, description: String) -> Unit,
    onDeleteGalleryItem: (GalleryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("Semua", "Class Meeting", "Study Tour", "Hari Guru", "Kegiatan", "Kenangan")

    val filteredItems = remember(galleryItems, selectedCategory) {
        if (selectedCategory == "Semua") {
            galleryItems
        } else {
            galleryItems.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_gallery_fab")
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Tambah Foto")
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
            // Category Filter Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category) },
                        leadingIcon = {
                            if (selectedCategory == category) Icon(Icons.Default.Check, contentDescription = null)
                        }
                    )
                }
            }

            // Gallery Feed
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("gallery_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (filteredItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada foto dalam kategori ini.",
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    items(filteredItems, key = { it.id }) { item ->
                        GalleryPostCard(
                            item = item,
                            isAdmin = isAdmin,
                            onToggleLike = { onToggleLike(item) },
                            onDelete = { onDeleteGalleryItem(item) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddGalleryItemDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, category, description ->
                onAddGalleryItem(title, category, description)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun GalleryPostCard(
    item: GalleryItem,
    isAdmin: Boolean,
    onToggleLike: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header: Category & Date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.category,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.date,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    if (isAdmin) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opsi",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Image Display / Colorful Memory Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ClassUiUtils.getAvatarColor(item.id.toInt()),
                                ClassUiUtils.getAvatarColor(item.id.toInt() + 2)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Footer: Title, Description, Likes
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onToggleLike() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (item.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Suka",
                            tint = if (item.isLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${item.likesCount} Suka",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = if (item.isLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "Dokumentasi Kelas XII MIPA 1",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
private fun AddGalleryItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, description: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Kegiatan") }
    var description by remember { mutableStateOf("") }

    val categories = listOf("Kegiatan", "Class Meeting", "Study Tour", "Hari Guru", "Kenangan")

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
                    text = "Tambah Momen & Foto Kenangan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Kegiatan *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Kategori:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Cerita / Deskripsi Momen *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
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
                            if (title.isNotBlank() && description.isNotBlank()) {
                                onConfirm(title.trim(), category, description.trim())
                            }
                        },
                        enabled = title.isNotBlank() && description.isNotBlank()
                    ) {
                        Text("Bagikan Momen")
                    }
                }
            }
        }
    }
}
