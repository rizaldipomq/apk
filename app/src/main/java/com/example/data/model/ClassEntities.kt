package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class_profile")
data class ClassProfile(
    @PrimaryKey val id: Int = 1,
    val className: String = "KELAS XI-TKJ",
    val schoolName: String = "SMK Negeri 1",
    val academicYear: String = "2024 / 2025",
    val slogan: String = "Teknik Komputer & Jaringan • Solid, Cerdas, Terkoneksi",
    val homeroomTeacher: String = "Budi Santoso, S.Kom., M.T.",
    val homeroomTeacherSubject: String = "Wali Kelas & Guru Produktif Jaringan",
    val classLeader: String = "Muhammad Rizaldi Pratama",
    val welcomeMessage: String = "Selamat datang di website resmi KELAS XI-TKJ. Portal informasi terpadu siswa Teknik Komputer & Jaringan!",
    val instagramHandle: String = "@xi_tkj.official",
    val heroImageUrl: String = "",
    val totalStudents: Int = 22,
    val maleStudents: Int = 14,
    val femaleStudents: Int = 8,
    val weeklyCashTarget: Long = 5000L
)

@Entity(tableName = "student_members")
data class StudentMember(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val absentNumber: Int,
    val name: String,
    val nickname: String,
    val gender: String, // "L" or "P"
    val role: String = "Anggota", // "Ketua Kelas", "Wakil Ketua", "Sekretaris 1", "Bendahara 1", etc.
    val nisn: String = "",
    val quote: String = "",
    val hobby: String = "",
    val dream: String = "",
    val instagram: String = "",
    val photoUrl: String = "",
    val avatarColorIndex: Int = 0
)

@Entity(tableName = "class_schedules")
data class ClassSchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: String, // "Senin", "Selasa", "Rabu", "Kamis", "Jumat"
    val orderNumber: Int,
    val subject: String,
    val timeRange: String,
    val teacher: String,
    val room: String = "Ruang XII-1"
)

@Entity(tableName = "picket_schedules")
data class PicketSchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: String,
    val members: String,
    val taskDescription: String = "Menyapu, mengepel, membersihkan papan tulis & membuang sampah"
)

@Entity(tableName = "cash_transactions")
data class CashTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Long,
    val type: String, // "INCOME" or "EXPENSE"
    val date: String,
    val category: String,
    val recordedBy: String = "Bendahara Kelas",
    val notes: String = ""
)

@Entity(tableName = "gallery_items")
data class GalleryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val date: String,
    val imageUrl: String = "",
    val description: String = "",
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Tugas/PR", "Pengumuman", "Ujian", "Kegiatan"
    val content: String,
    val dueDate: String = "",
    val author: String = "Pengurus Kelas",
    val datePosted: String = "",
    val isCompleted: Boolean = false
)

@Entity(tableName = "spotify_tracks")
data class SpotifyTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val artist: String,
    val album: String,
    val durationText: String = "0:30 (Preview)",
    val coverUrl: String = "",
    val audioUrl: String = "",
    val spotifyUri: String = ""
)

