package com.example.data.repository

import com.example.data.InitialData
import com.example.data.dao.ClassDao
import com.example.data.model.*
import com.example.data.sync.SyncClassData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClassRepository(private val classDao: ClassDao) {

    val classProfile: Flow<ClassProfile?> = classDao.getClassProfile()
    val allStudents: Flow<List<StudentMember>> = classDao.getAllStudents()
    val allSchedules: Flow<List<ClassSchedule>> = classDao.getAllSchedules()
    val allPickets: Flow<List<PicketSchedule>> = classDao.getAllPicketSchedules()
    val allTransactions: Flow<List<CashTransaction>> = classDao.getAllTransactions()
    val allGalleryItems: Flow<List<GalleryItem>> = classDao.getAllGalleryItems()
    val allAnnouncements: Flow<List<Announcement>> = classDao.getAllAnnouncements()
    val allSpotifyTracks: Flow<List<SpotifyTrackEntity>> = classDao.getAllSpotifyTracks()

    fun getSchedulesByDay(day: String): Flow<List<ClassSchedule>> =
        classDao.getSchedulesByDay(day)

    val cashSummary: Flow<CashSummary> = classDao.getAllTransactions().map { transactions ->
        var income = 0L
        var expense = 0L
        for (tx in transactions) {
            if (tx.type.equals("INCOME", ignoreCase = true)) {
                income += tx.amount
            } else {
                expense += tx.amount
            }
        }
        CashSummary(
            totalIncome = income,
            totalExpense = expense,
            currentBalance = income - expense,
            totalTransactions = transactions.size
        )
    }

    suspend fun updateProfile(profile: ClassProfile) {
        classDao.insertOrUpdateProfile(profile)
    }

    suspend fun addStudent(student: StudentMember): Long {
        return classDao.insertStudent(student)
    }

    suspend fun updateStudent(student: StudentMember) {
        classDao.updateStudent(student)
    }

    suspend fun deleteStudent(student: StudentMember) {
        classDao.deleteStudent(student)
    }

    suspend fun addSchedule(schedule: ClassSchedule): Long {
        return classDao.insertSchedule(schedule)
    }

    suspend fun deleteSchedule(schedule: ClassSchedule) {
        classDao.deleteSchedule(schedule)
    }

    suspend fun updatePicket(picket: PicketSchedule) {
        classDao.updatePicket(picket)
    }

    suspend fun addTransaction(transaction: CashTransaction): Long {
        return classDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: CashTransaction) {
        classDao.deleteTransaction(transaction)
    }

    suspend fun addGalleryItem(item: GalleryItem): Long {
        return classDao.insertGalleryItem(item)
    }

    suspend fun toggleGalleryLike(id: Long, currentLikes: Int, isLiked: Boolean) {
        val newLikes = if (isLiked) (currentLikes - 1).coerceAtLeast(0) else currentLikes + 1
        classDao.updateLike(id, newLikes, !isLiked)
    }

    suspend fun deleteGalleryItem(item: GalleryItem) {
        classDao.deleteGalleryItem(item)
    }

    suspend fun addAnnouncement(announcement: Announcement): Long {
        return classDao.insertAnnouncement(announcement)
    }

    suspend fun toggleAnnouncementCompleted(id: Long, completed: Boolean) {
        classDao.toggleAnnouncementCompleted(id, completed)
    }

    suspend fun deleteAnnouncement(announcement: Announcement) {
        classDao.deleteAnnouncement(announcement)
    }

    suspend fun addSpotifyTrack(track: SpotifyTrackEntity): Long {
        return classDao.insertSpotifyTrack(track)
    }

    suspend fun updateSpotifyTrack(track: SpotifyTrackEntity) {
        classDao.updateSpotifyTrack(track)
    }

    suspend fun deleteSpotifyTrack(track: SpotifyTrackEntity) {
        classDao.deleteSpotifyTrack(track)
    }

    suspend fun importSyncData(data: SyncClassData) {
        data.profile?.let { classDao.insertOrUpdateProfile(it) }

        if (data.students.isNotEmpty()) {
            classDao.clearAllStudents()
            classDao.insertStudents(data.students)
        }
        if (data.schedules.isNotEmpty()) {
            classDao.clearAllSchedules()
            classDao.insertSchedules(data.schedules)
        }
        if (data.pickets.isNotEmpty()) {
            classDao.clearAllPickets()
            classDao.insertPickets(data.pickets)
        }
        if (data.transactions.isNotEmpty()) {
            classDao.clearAllTransactions()
            classDao.insertTransactions(data.transactions)
        }
        if (data.gallery.isNotEmpty()) {
            classDao.clearAllGallery()
            classDao.insertGalleryItems(data.gallery)
        }
        if (data.announcements.isNotEmpty()) {
            classDao.clearAllAnnouncements()
            classDao.insertAnnouncements(data.announcements)
        }
        if (data.spotifyTracks.isNotEmpty()) {
            classDao.clearAllSpotifyTracks()
            classDao.insertSpotifyTracks(data.spotifyTracks)
        }
    }

    suspend fun resetToDefault() {
        classDao.clearAllStudents()
        classDao.clearAllSchedules()
        classDao.clearAllPickets()
        classDao.clearAllTransactions()
        classDao.clearAllGallery()
        classDao.clearAllAnnouncements()
        classDao.clearAllSpotifyTracks()

        classDao.insertOrUpdateProfile(InitialData.getDefaultProfile())
        classDao.insertStudents(InitialData.getDefaultStudents())
        classDao.insertSchedules(InitialData.getDefaultSchedules())
        classDao.insertPickets(InitialData.getDefaultPickets())
        classDao.insertTransactions(InitialData.getDefaultTransactions())
        classDao.insertGalleryItems(InitialData.getDefaultGallery())
        classDao.insertAnnouncements(InitialData.getDefaultAnnouncements())
        classDao.insertSpotifyTracks(InitialData.getDefaultSpotifyTracks())
    }
}

data class CashSummary(
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val currentBalance: Long = 0L,
    val totalTransactions: Int = 0
)
