package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassDao {

    // --- Profile ---
    @Query("SELECT * FROM class_profile WHERE id = 1")
    fun getClassProfile(): Flow<ClassProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ClassProfile)

    // --- Students / Members ---
    @Query("SELECT * FROM student_members ORDER BY absentNumber ASC")
    fun getAllStudents(): Flow<List<StudentMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentMember>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentMember): Long

    @Update
    suspend fun updateStudent(student: StudentMember)

    @Delete
    suspend fun deleteStudent(student: StudentMember)

    @Query("DELETE FROM student_members")
    suspend fun clearAllStudents()

    // --- Schedules ---
    @Query("SELECT * FROM class_schedules WHERE dayOfWeek = :day ORDER BY orderNumber ASC")
    fun getSchedulesByDay(day: String): Flow<List<ClassSchedule>>

    @Query("SELECT * FROM class_schedules ORDER BY dayOfWeek, orderNumber ASC")
    fun getAllSchedules(): Flow<List<ClassSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<ClassSchedule>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ClassSchedule): Long

    @Delete
    suspend fun deleteSchedule(schedule: ClassSchedule)

    @Query("DELETE FROM class_schedules")
    suspend fun clearAllSchedules()

    // --- Pickets ---
    @Query("SELECT * FROM picket_schedules ORDER BY id ASC")
    fun getAllPicketSchedules(): Flow<List<PicketSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPickets(pickets: List<PicketSchedule>)

    @Update
    suspend fun updatePicket(picket: PicketSchedule)

    @Query("DELETE FROM picket_schedules")
    suspend fun clearAllPickets()

    // --- Cash Transactions ---
    @Query("SELECT * FROM cash_transactions ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<CashTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CashTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<CashTransaction>)

    @Delete
    suspend fun deleteTransaction(transaction: CashTransaction)

    @Query("DELETE FROM cash_transactions")
    suspend fun clearAllTransactions()

    // --- Gallery ---
    @Query("SELECT * FROM gallery_items ORDER BY id DESC")
    fun getAllGalleryItems(): Flow<List<GalleryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryItems(items: List<GalleryItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryItem(item: GalleryItem): Long

    @Update
    suspend fun updateGalleryItem(item: GalleryItem)

    @Delete
    suspend fun deleteGalleryItem(item: GalleryItem)

    @Query("UPDATE gallery_items SET likesCount = :newLikes, isLiked = :isLiked WHERE id = :id")
    suspend fun updateLike(id: Long, newLikes: Int, isLiked: Boolean)

    @Query("DELETE FROM gallery_items")
    suspend fun clearAllGallery()

    // --- Announcements / Tasks ---
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<Announcement>)

    @Update
    suspend fun updateAnnouncement(announcement: Announcement)

    @Delete
    suspend fun deleteAnnouncement(announcement: Announcement)

    @Query("UPDATE announcements SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleAnnouncementCompleted(id: Long, completed: Boolean)

    @Query("DELETE FROM announcements")
    suspend fun clearAllAnnouncements()

    // --- Spotify Playlist ---
    @Query("SELECT * FROM spotify_tracks ORDER BY id ASC")
    fun getAllSpotifyTracks(): Flow<List<SpotifyTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpotifyTracks(tracks: List<SpotifyTrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpotifyTrack(track: SpotifyTrackEntity): Long

    @Update
    suspend fun updateSpotifyTrack(track: SpotifyTrackEntity)

    @Delete
    suspend fun deleteSpotifyTrack(track: SpotifyTrackEntity)

    @Query("DELETE FROM spotify_tracks")
    suspend fun clearAllSpotifyTracks()
}
