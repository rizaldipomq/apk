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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CashTransaction
import com.example.data.repository.CashSummary
import com.example.ui.util.ClassUiUtils

@Composable
fun ClassFinanceScreen(
    summary: CashSummary,
    transactions: List<CashTransaction>,
    isAdmin: Boolean = false,
    onAddTransaction: (title: String, amount: Long, type: String, category: String, notes: String) -> Unit,
    onDeleteTransaction: (CashTransaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, INCOME, EXPENSE
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredList = remember(transactions, selectedFilter) {
        when (selectedFilter) {
            "INCOME" -> transactions.filter { it.type.equals("INCOME", ignoreCase = true) }
            "EXPENSE" -> transactions.filter { it.type.equals("EXPENSE", ignoreCase = true) }
            else -> transactions
        }
    }

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_transaction_fab")
                ) {
                    Icon(Icons.Default.AddCard, contentDescription = "Catat Kas Baru")
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("finance_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Balance Summary Card with Gradient
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF1E3A8A),
                                        Color(0xFF0F766E)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SALDO KAS KELAS AKTIF",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBAE6FD),
                                    letterSpacing = 1.sp
                                )
                                Surface(
                                    color = Color(0x33FFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Transparan & Akurat",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = ClassUiUtils.formatRupiah(summary.currentBalance),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Breakdown Row: Total Masuk & Total Keluar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(Color(0xFF34D399), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.ArrowDownward,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Total Masuk",
                                            fontSize = 12.sp,
                                            color = Color(0xFFE2E8F0)
                                        )
                                    }
                                    Text(
                                        text = ClassUiUtils.formatRupiah(summary.totalIncome),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6EE7B7)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(Color(0xFFF87171), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.ArrowUpward,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Total Keluar",
                                            fontSize = 12.sp,
                                            color = Color(0xFFE2E8F0)
                                        )
                                    }
                                    Text(
                                        text = ClassUiUtils.formatRupiah(summary.totalExpense),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFCA5A5)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Info Iuran Mingguan
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Iuran Rutin Kelas: Rp 5.000 / minggu",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Disetor setiap hari Jumat ke Bendahara Kelas (Almira / Cantika)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 3. Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("Semua (${transactions.size})") }
                    )
                    FilterChip(
                        selected = selectedFilter == "INCOME",
                        onClick = { selectedFilter = "INCOME" },
                        label = { Text("Pemasukan") }
                    )
                    FilterChip(
                        selected = selectedFilter == "EXPENSE",
                        onClick = { selectedFilter = "EXPENSE" },
                        label = { Text("Pengeluaran") }
                    )
                }
            }

            // 4. Transaction List
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada transaksi di kategori ini.",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { tx ->
                    TransactionItemCard(
                        transaction = tx,
                        isAdmin = isAdmin,
                        onDelete = { onDeleteTransaction(tx) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, amount, type, category, notes ->
                onAddTransaction(title, amount, type, category, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TransactionItemCard(
    transaction: CashTransaction,
    isAdmin: Boolean,
    onDelete: () -> Unit
) {
    val isIncome = transaction.type.equals("INCOME", ignoreCase = true)
    val indicatorColor = if (isIncome) Color(0xFF10B981) else Color(0xFFEF4444)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(indicatorColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncome) Icons.Default.Add else Icons.Default.Remove,
                    contentDescription = null,
                    tint = indicatorColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${transaction.date} • ${transaction.category}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (transaction.notes.isNotBlank()) {
                    Text(
                        text = transaction.notes,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isIncome) "+" else "-"} ${ClassUiUtils.formatRupiah(transaction.amount)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = indicatorColor
                )
                if (isAdmin) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, amount: Long, type: String, category: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("INCOME") } // INCOME or EXPENSE
    var category by remember { mutableStateOf("Kas Mingguan") }
    var notes by remember { mutableStateOf("") }

    val categories = if (type == "INCOME") {
        listOf("Kas Mingguan", "Bazar & Event", "Donasi / Sumbangan", "Saldo Awal", "Lainnya")
    } else {
        listOf("Alat Tulis", "Acara Kelas", "Sosial & Jenguk", "Kebersihan", "Lainnya")
    }

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
                    text = "Catat Transaksi Kas Kelas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Type Toggle
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            type = "INCOME"
                            category = "Kas Mingguan"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "INCOME") Color(0xFF10B981) else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (type == "INCOME") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pemasukan (+)")
                    }

                    Button(
                        onClick = {
                            type = "EXPENSE"
                            category = "Alat Tulis"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "EXPENSE") Color(0xFFEF4444) else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (type == "EXPENSE") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pengeluaran (-)")
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Keterangan Singkat *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Jumlah Nominal (Rp) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Category selector
                Text("Kategori:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan / Rincian Tambahan") },
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
                            val nominal = amountText.filter { it.isDigit() }.toLongOrNull() ?: 0L
                            if (title.isNotBlank() && nominal > 0) {
                                onConfirm(title.trim(), nominal, type, category, notes.trim())
                            }
                        },
                        enabled = title.isNotBlank() && (amountText.filter { it.isDigit() }.toLongOrNull() ?: 0L) > 0
                    ) {
                        Text("Simpan Transaksi")
                    }
                }
            }
        }
    }
}
