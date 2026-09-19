package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.viewmodel.ClassViewModel
import kotlinx.coroutines.flow.collectLatest

sealed class ClassNavigationItem(
    val index: Int,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : ClassNavigationItem(0, "Beranda", Icons.Filled.Home, Icons.Outlined.Home)
    object Members : ClassNavigationItem(1, "Siswa", Icons.Filled.People, Icons.Outlined.People)
    object Schedule : ClassNavigationItem(2, "Jadwal", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarToday)
    object Finance : ClassNavigationItem(3, "Kas", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet)
    object Gallery : ClassNavigationItem(4, "Galeri", Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary)
    object Bulletin : ClassNavigationItem(5, "Mading", Icons.Filled.Campaign, Icons.Outlined.Campaign)
    object Admin : ClassNavigationItem(6, "Kelola", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassMainScreen(
    viewModel: ClassViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val classProfile by viewModel.classProfile.collectAsStateWithLifecycle()
    val students by viewModel.allStudents.collectAsStateWithLifecycle()
    val schedules by viewModel.allSchedules.collectAsStateWithLifecycle()
    val pickets by viewModel.allPickets.collectAsStateWithLifecycle()
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val cashSummary by viewModel.cashSummary.collectAsStateWithLifecycle()
    val galleryItems by viewModel.allGalleryItems.collectAsStateWithLifecycle()
    val announcements by viewModel.allAnnouncements.collectAsStateWithLifecycle()
    val spotifyTracks by viewModel.allSpotifyTracks.collectAsStateWithLifecycle()

    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    val selectedDay by viewModel.selectedDay.collectAsStateWithLifecycle()
    val searchQuery by viewModel.studentSearchQuery.collectAsStateWithLifecycle()
    val genderFilter by viewModel.studentGenderFilter.collectAsStateWithLifecycle()
    val galleryCategory by viewModel.galleryCategoryFilter.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.messageEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val bottomNavItems = listOf(
        ClassNavigationItem.Home,
        ClassNavigationItem.Members,
        ClassNavigationItem.Schedule,
        ClassNavigationItem.Finance,
        ClassNavigationItem.Gallery
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = classProfile.className,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = classProfile.schoolName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Mading shortcut
                    IconButton(
                        onClick = { viewModel.setTab(5) },
                        modifier = Modifier.testTag("top_bulletin_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (announcements.any { !it.isCompleted }) {
                                    Badge { Text("${announcements.count { !it.isCompleted }}") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentTab == 5) Icons.Filled.Campaign else Icons.Outlined.Campaign,
                                contentDescription = "Mading & Tugas",
                                tint = if (currentTab == 5) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Admin / Settings
                    IconButton(
                        onClick = { viewModel.setTab(6) },
                        modifier = Modifier.testTag("top_admin_button")
                    ) {
                        Icon(
                            imageVector = if (currentTab == 6) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Kelola Kelas",
                            tint = if (currentTab == 6) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                windowInsets = WindowInsets.navigationBars
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentTab == item.index
                    NavigationBarItem(
                        selected = selected,
                        onClick = { viewModel.setTab(item.index) },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier.testTag("nav_item_${item.title.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(280)) + scaleIn(initialScale = 0.98f, animationSpec = tween(280)) togetherWith
                            fadeOut(animationSpec = tween(200))
                },
                label = "tab_animation"
            ) { tab ->
                when (tab) {
                    0 -> ClassHomeScreen(
                        profile = classProfile,
                        cashSummary = cashSummary,
                        todaySchedules = schedules.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) },
                        recentAnnouncements = announcements,
                        spotifyTracks = spotifyTracks,
                        onNavigateTab = { viewModel.setTab(it) }
                    )

                    1 -> ClassMembersScreen(
                        profile = classProfile,
                        students = students,
                        searchQuery = searchQuery,
                        genderFilter = genderFilter,
                        isAdmin = isAdmin,
                        onSearchChange = { viewModel.setStudentSearchQuery(it) },
                        onGenderFilterChange = { viewModel.setStudentGenderFilter(it) },
                        onAddStudent = { viewModel.addStudent(it) },
                        onUpdateStudent = { viewModel.updateStudent(it) },
                        onDeleteStudent = { viewModel.deleteStudent(it) }
                    )

                    2 -> ClassScheduleScreen(
                        schedules = schedules,
                        pickets = pickets,
                        selectedDay = selectedDay,
                        isAdmin = isAdmin,
                        onSelectDay = { viewModel.setSelectedDay(it) },
                        onAddSchedule = { viewModel.addSchedule(it) },
                        onDeleteSchedule = { viewModel.deleteSchedule(it) },
                        onUpdatePicket = { viewModel.updatePicket(it) }
                    )

                    3 -> ClassFinanceScreen(
                        summary = cashSummary,
                        transactions = transactions,
                        isAdmin = isAdmin,
                        onAddTransaction = { title, amount, type, category, notes ->
                            viewModel.addTransaction(title, amount, type, category, notes)
                        },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) }
                    )

                    4 -> ClassGalleryScreen(
                        galleryItems = galleryItems,
                        selectedCategory = galleryCategory,
                        isAdmin = isAdmin,
                        onCategoryChange = { viewModel.setGalleryCategoryFilter(it) },
                        onToggleLike = { viewModel.toggleGalleryLike(it) },
                        onAddGalleryItem = { title, category, description ->
                            viewModel.addGalleryItem(title, category, description)
                        },
                        onDeleteGalleryItem = { viewModel.deleteGalleryItem(it) }
                    )

                    5 -> ClassBulletinScreen(
                        announcements = announcements,
                        isAdmin = isAdmin,
                        onToggleCompleted = { viewModel.toggleAnnouncementCompleted(it.id, it.isCompleted) },
                        onAddAnnouncement = { title, category, content, dueDate ->
                            viewModel.addAnnouncement(title, category, content, dueDate)
                        },
                        onDeleteAnnouncement = { viewModel.deleteAnnouncement(it) }
                    )

                    6 -> ClassAdminScreen(
                        profile = classProfile,
                        isAdmin = isAdmin,
                        isSyncing = isSyncing,
                        students = students,
                        schedules = schedules,
                        pickets = pickets,
                        transactions = transactions,
                        galleryItems = galleryItems,
                        announcements = announcements,
                        spotifyTracks = spotifyTracks,
                        onLogin = { user, pass -> viewModel.loginAdmin(user, pass) },
                        onLogout = { viewModel.logoutAdmin() },
                        onSyncFromCloud = { viewModel.syncFromCloud() },
                        onPushToCloud = { viewModel.pushToCloud() },
                        onUpdateProfile = { viewModel.updateProfile(it) },
                        onAddStudent = { viewModel.addStudent(it) },
                        onUpdateStudent = { viewModel.updateStudent(it) },
                        onDeleteStudent = { viewModel.deleteStudent(it) },
                        onAddSchedule = { viewModel.addSchedule(it) },
                        onDeleteSchedule = { viewModel.deleteSchedule(it) },
                        onUpdatePicket = { viewModel.updatePicket(it) },
                        onAddTransaction = { title, amount, type, category, notes ->
                            viewModel.addTransaction(title, amount, type, category, notes)
                        },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) },
                        onAddGalleryItem = { title, category, description, imageUrl ->
                            viewModel.addGalleryItem(title, category, description, imageUrl)
                        },
                        onDeleteGalleryItem = { viewModel.deleteGalleryItem(it) },
                        onAddAnnouncement = { title, category, content, dueDate ->
                            viewModel.addAnnouncement(title, category, content, dueDate)
                        },
                        onDeleteAnnouncement = { viewModel.deleteAnnouncement(it) },
                        onAddSpotifyTrack = { viewModel.addSpotifyTrack(it) },
                        onDeleteSpotifyTrack = { viewModel.deleteSpotifyTrack(it) },
                        onResetData = { viewModel.resetDataToDefault() },
                        onNavigateTab = { viewModel.setTab(it) }
                    )
                }
            }
        }
    }
}
