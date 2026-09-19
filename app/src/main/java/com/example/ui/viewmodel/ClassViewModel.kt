package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ClassDatabase
import com.example.data.InitialData
import com.example.data.model.*
import com.example.data.repository.CashSummary
import com.example.data.repository.ClassRepository
import com.example.data.sync.ClassCloudSyncService
import com.example.data.sync.SyncClassData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ClassViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClassRepository
    private val cloudSyncService = ClassCloudSyncService()

    // --- Admin Authentication State ---
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    // --- Cloud Sync State ---
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncStatus = MutableStateFlow("Terhubung ke Cloud XI-TKJ")
    val lastSyncStatus: StateFlow<String> = _lastSyncStatus.asStateFlow()

    init {
        val database = ClassDatabase.getDatabase(application, viewModelScope)
        repository = ClassRepository(database.classDao())

        // Otomatis sinkronisasi data terbaru dari Cloud saat aplikasi dibuka
        syncFromCloud(silent = true)
    }

    // --- State Flows ---
    val classProfile: StateFlow<ClassProfile> = repository.classProfile
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InitialData.getDefaultProfile()
        )

    val allStudents: StateFlow<List<StudentMember>> = repository.allStudents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InitialData.getDefaultStudents()
        )

    val allSchedules: StateFlow<List<ClassSchedule>> = repository.allSchedules
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InitialData.getDefaultSchedules()
        )

    val allPickets: StateFlow<List<PicketSchedule>> = repository.allPickets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InitialData.getDefaultPickets()
        )

    val allTransactions: StateFlow<List<CashTransaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InitialData.getDefaultTransactions()
        )

    val cashSummary: StateFlow<CashSummary> = repository.cashSummary
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CashSummary(
                totalIncome = 1220000L,
                totalExpense = 170000L,
                currentBalance = 1050000L,
                totalTransactions = 6
            )
        )

    val allGalleryItems: StateFlow<List<GalleryItem>> = repository.allGalleryItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InitialData.getDefaultGallery()
        )

    val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InitialData.getDefaultAnnouncements()
        )

    val allSpotifyTracks: StateFlow<List<SpotifyTrackEntity>> = repository.allSpotifyTracks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InitialData.getDefaultSpotifyTracks()
        )

    // --- UI Filters & Navigation ---
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _selectedDay = MutableStateFlow("Senin")
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    private val _studentSearchQuery = MutableStateFlow("")
    val studentSearchQuery: StateFlow<String> = _studentSearchQuery.asStateFlow()

    private val _studentGenderFilter = MutableStateFlow("ALL")
    val studentGenderFilter: StateFlow<String> = _studentGenderFilter.asStateFlow()

    private val _galleryCategoryFilter = MutableStateFlow("Semua")
    val galleryCategoryFilter: StateFlow<String> = _galleryCategoryFilter.asStateFlow()

    private val _messageEvent = MutableSharedFlow<String>()
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

    fun setTab(tabIndex: Int) {
        _currentTab.value = tabIndex
    }

    fun setSelectedDay(day: String) {
        _selectedDay.value = day
    }

    fun setStudentSearchQuery(query: String) {
        _studentSearchQuery.value = query
    }

    fun setStudentGenderFilter(filter: String) {
        _studentGenderFilter.value = filter
    }

    fun setGalleryCategoryFilter(category: String) {
        _galleryCategoryFilter.value = category
    }

    // --- Admin Authentication ---
    fun loginAdmin(user: String, pass: String): Boolean {
        if (user.trim() == "rizaldi" && pass == "rizaldi##") {
            _isAdmin.value = true
            viewModelScope.launch {
                _messageEvent.emit("Selamat datang, Admin Rizaldi! Akses penuh dibuka.")
            }
            return true
        } else {
            viewModelScope.launch {
                _messageEvent.emit("Username atau Password Admin salah!")
            }
            return false
        }
    }

    fun logoutAdmin() {
        _isAdmin.value = false
        viewModelScope.launch {
            _messageEvent.emit("Keluar dari Mode Admin. Mode Pengunjung aktif.")
        }
    }

    // --- Cloud Synchronization ---
    fun syncFromCloud(silent: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSyncing.value = true
            try {
                val cloudData = cloudSyncService.fetchCloudData()
                if (cloudData != null && (cloudData.students.isNotEmpty() || cloudData.profile != null)) {
                    repository.importSyncData(cloudData)
                    _lastSyncStatus.value = "Tersinkronkan Real-Time"
                    if (!silent) {
                        _messageEvent.emit("Berhasil sinkronisasi data terbaru dari cloud!")
                    }
                } else {
                    _lastSyncStatus.value = "Menggunakan data lokal"
                    if (!silent) {
                        _messageEvent.emit("Cloud siap atau data lokal sudah yang terbaru.")
                    }
                }
            } catch (e: Exception) {
                _lastSyncStatus.value = "Offline (Cache Lokal)"
                if (!silent) {
                    _messageEvent.emit("Mode offline aktif. Menggunakan penyimpanan lokal.")
                }
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun pushToCloud() {
        viewModelScope.launch(Dispatchers.IO) {
            _isSyncing.value = true
            try {
                val syncData = SyncClassData(
                    profile = classProfile.value,
                    students = allStudents.value,
                    schedules = allSchedules.value,
                    pickets = allPickets.value,
                    transactions = allTransactions.value,
                    gallery = allGalleryItems.value,
                    announcements = allAnnouncements.value,
                    spotifyTracks = allSpotifyTracks.value
                )
                val success = cloudSyncService.pushCloudData(syncData)
                if (success) {
                    _lastSyncStatus.value = "Semua Perubahan Tersimpan di Cloud"
                    _messageEvent.emit("Perubahan berhasil dikirim ke Cloud! Semua pengguna APK akan melihat update ini.")
                } else {
                    _messageEvent.emit("Data tersimpan lokal, gagal upload ke cloud (periksa internet).")
                }
            } catch (e: Exception) {
                _messageEvent.emit("Data tersimpan lokal.")
            } finally {
                _isSyncing.value = false
            }
        }
    }

    // --- CRUD Actions (Auto-sync to cloud when Admin saves) ---
    fun updateProfile(profile: ClassProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProfile(profile)
            _messageEvent.emit("Profil website kelas berhasil disimpan!")
            pushToCloud()
        }
    }

    fun addStudent(student: StudentMember) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addStudent(student)
            _messageEvent.emit("Data siswa '${student.name}' berhasil ditambahkan!")
            pushToCloud()
        }
    }

    fun updateStudent(student: StudentMember) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStudent(student)
            _messageEvent.emit("Data siswa '${student.name}' berhasil diperbarui!")
            pushToCloud()
        }
    }

    fun deleteStudent(student: StudentMember) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStudent(student)
            _messageEvent.emit("Siswa '${student.name}' berhasil dihapus.")
            pushToCloud()
        }
    }

    fun addSchedule(schedule: ClassSchedule) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSchedule(schedule)
            _messageEvent.emit("Jadwal '${schedule.subject}' berhasil ditambahkan!")
            pushToCloud()
        }
    }

    fun deleteSchedule(schedule: ClassSchedule) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSchedule(schedule)
            _messageEvent.emit("Jadwal pelajaran dihapus.")
            pushToCloud()
        }
    }

    fun updatePicket(picket: PicketSchedule) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePicket(picket)
            _messageEvent.emit("Jadwal piket ${picket.dayOfWeek} diperbarui!")
            pushToCloud()
        }
    }

    fun addTransaction(title: String, amount: Long, type: String, category: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID")).format(java.util.Date())
            val tx = CashTransaction(
                title = title,
                amount = amount,
                type = type,
                date = dateStr,
                category = category,
                notes = notes
            )
            repository.addTransaction(tx)
            _messageEvent.emit("Transaksi kas berhasil dicatat!")
            pushToCloud()
        }
    }

    fun deleteTransaction(transaction: CashTransaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(transaction)
            _messageEvent.emit("Catatan transaksi kas dihapus.")
            pushToCloud()
        }
    }

    fun toggleGalleryLike(item: GalleryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleGalleryLike(item.id, item.likesCount, item.isLiked)
        }
    }

    fun addGalleryItem(title: String, category: String, description: String, imageUrl: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID")).format(java.util.Date())
            val item = GalleryItem(
                title = title,
                category = category,
                date = dateStr,
                imageUrl = imageUrl,
                description = description,
                likesCount = 1,
                isLiked = true
            )
            repository.addGalleryItem(item)
            _messageEvent.emit("Foto kenangan berhasil ditambahkan!")
            pushToCloud()
        }
    }

    fun deleteGalleryItem(item: GalleryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteGalleryItem(item)
            _messageEvent.emit("Foto kenangan dihapus.")
            pushToCloud()
        }
    }

    fun addAnnouncement(title: String, category: String, content: String, dueDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID")).format(java.util.Date())
            val announcement = Announcement(
                title = title,
                category = category,
                content = content,
                dueDate = dueDate,
                datePosted = dateStr,
                isCompleted = false
            )
            repository.addAnnouncement(announcement)
            _messageEvent.emit("Pengumuman / tugas baru berhasil dipublikasikan!")
            pushToCloud()
        }
    }

    fun toggleAnnouncementCompleted(id: Long, currentCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleAnnouncementCompleted(id, !currentCompleted)
            pushToCloud()
        }
    }

    fun deleteAnnouncement(announcement: Announcement) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAnnouncement(announcement)
            _messageEvent.emit("Pengumuman dihapus.")
            pushToCloud()
        }
    }

    // --- Spotify Playlist CRUD ---
    fun addSpotifyTrack(track: SpotifyTrackEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSpotifyTrack(track)
            _messageEvent.emit("Lagu '${track.title}' ditambahkan ke Spotify playlist!")
            pushToCloud()
        }
    }

    fun updateSpotifyTrack(track: SpotifyTrackEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSpotifyTrack(track)
            _messageEvent.emit("Lagu '${track.title}' diperbarui!")
            pushToCloud()
        }
    }

    fun deleteSpotifyTrack(track: SpotifyTrackEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSpotifyTrack(track)
            _messageEvent.emit("Lagu '${track.title}' dihapus dari playlist.")
            pushToCloud()
        }
    }

    fun resetDataToDefault() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetToDefault()
            _messageEvent.emit("Data website kelas berhasil direset ke setelan awal!")
            pushToCloud()
        }
    }
}

class ClassViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClassViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClassViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
