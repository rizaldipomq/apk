package com.example.data.sync

import android.util.Log
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class SyncClassData(
    val profile: ClassProfile?,
    val students: List<StudentMember>,
    val schedules: List<ClassSchedule>,
    val pickets: List<PicketSchedule>,
    val transactions: List<CashTransaction>,
    val gallery: List<GalleryItem>,
    val announcements: List<Announcement>,
    val spotifyTracks: List<SpotifyTrackEntity>
)

class ClassCloudSyncService {

    companion object {
        private const val TAG = "ClassCloudSyncService"
        // Online real-time sync endpoint for KELAS XI-TKJ
        private const val CLOUD_ENDPOINT = "https://api.restful-api.dev/objects/ff808181a09d98f701a0ba6bd70a482e"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun fetchCloudData(): SyncClassData? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(CLOUD_ENDPOINT)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(TAG, "Fetch failed: ${response.code}")
                    return@withContext null
                }

                val bodyString = response.body?.string() ?: return@withContext null
                val rootJson = JSONObject(bodyString)
                val dataJson = rootJson.optJSONObject("data") ?: return@withContext null

                parseSyncData(dataJson)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching cloud data: ${e.message}")
            null
        }
    }

    suspend fun pushCloudData(syncData: SyncClassData): Boolean = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("name", "KELAS XI-TKJ")
                put("data", serializeSyncData(syncData))
            }

            val body = payload.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(CLOUD_ENDPOINT)
                .put(body)
                .build()

            client.newCall(request).execute().use { response ->
                val success = response.isSuccessful
                Log.d(TAG, "Push cloud data success: $success (code ${response.code})")
                success
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pushing cloud data: ${e.message}")
            false
        }
    }

    private fun serializeSyncData(data: SyncClassData): JSONObject {
        val root = JSONObject()

        // Profile
        data.profile?.let { p ->
            val pObj = JSONObject().apply {
                put("className", p.className)
                put("schoolName", p.schoolName)
                put("academicYear", p.academicYear)
                put("slogan", p.slogan)
                put("homeroomTeacher", p.homeroomTeacher)
                put("homeroomTeacherSubject", p.homeroomTeacherSubject)
                put("classLeader", p.classLeader)
                put("welcomeMessage", p.welcomeMessage)
                put("instagramHandle", p.instagramHandle)
                put("heroImageUrl", p.heroImageUrl)
                put("totalStudents", p.totalStudents)
                put("maleStudents", p.maleStudents)
                put("femaleStudents", p.femaleStudents)
                put("weeklyCashTarget", p.weeklyCashTarget)
            }
            root.put("profile", pObj)
        }

        // Students
        val studentsArr = JSONArray()
        data.students.forEach { s ->
            studentsArr.put(JSONObject().apply {
                put("id", s.id)
                put("absentNumber", s.absentNumber)
                put("name", s.name)
                put("nickname", s.nickname)
                put("gender", s.gender)
                put("role", s.role)
                put("nisn", s.nisn)
                put("quote", s.quote)
                put("hobby", s.hobby)
                put("dream", s.dream)
                put("instagram", s.instagram)
                put("photoUrl", s.photoUrl)
                put("avatarColorIndex", s.avatarColorIndex)
            })
        }
        root.put("students", studentsArr)

        // Schedules
        val schedArr = JSONArray()
        data.schedules.forEach { sc ->
            schedArr.put(JSONObject().apply {
                put("id", sc.id)
                put("dayOfWeek", sc.dayOfWeek)
                put("orderNumber", sc.orderNumber)
                put("subject", sc.subject)
                put("timeRange", sc.timeRange)
                put("teacher", sc.teacher)
                put("room", sc.room)
            })
        }
        root.put("schedules", schedArr)

        // Pickets
        val picketsArr = JSONArray()
        data.pickets.forEach { pk ->
            picketsArr.put(JSONObject().apply {
                put("id", pk.id)
                put("dayOfWeek", pk.dayOfWeek)
                put("members", pk.members)
                put("taskDescription", pk.taskDescription)
            })
        }
        root.put("pickets", picketsArr)

        // Transactions
        val transArr = JSONArray()
        data.transactions.forEach { tr ->
            transArr.put(JSONObject().apply {
                put("id", tr.id)
                put("title", tr.title)
                put("amount", tr.amount)
                put("type", tr.type)
                put("date", tr.date)
                put("category", tr.category)
                put("recordedBy", tr.recordedBy)
                put("notes", tr.notes)
            })
        }
        root.put("transactions", transArr)

        // Gallery
        val galArr = JSONArray()
        data.gallery.forEach { g ->
            galArr.put(JSONObject().apply {
                put("id", g.id)
                put("title", g.title)
                put("category", g.category)
                put("date", g.date)
                put("imageUrl", g.imageUrl)
                put("description", g.description)
                put("likesCount", g.likesCount)
                put("isLiked", g.isLiked)
            })
        }
        root.put("gallery", galArr)

        // Announcements
        val annArr = JSONArray()
        data.announcements.forEach { a ->
            annArr.put(JSONObject().apply {
                put("id", a.id)
                put("title", a.title)
                put("category", a.category)
                put("content", a.content)
                put("dueDate", a.dueDate)
                put("author", a.author)
                put("datePosted", a.datePosted)
                put("isCompleted", a.isCompleted)
            })
        }
        root.put("announcements", annArr)

        // Spotify Tracks
        val spArr = JSONArray()
        data.spotifyTracks.forEach { st ->
            spArr.put(JSONObject().apply {
                put("id", st.id)
                put("title", st.title)
                put("artist", st.artist)
                put("album", st.album)
                put("durationText", st.durationText)
                put("coverUrl", st.coverUrl)
                put("audioUrl", st.audioUrl)
                put("spotifyUri", st.spotifyUri)
            })
        }
        root.put("spotifyTracks", spArr)

        root.put("updatedTimestamp", System.currentTimeMillis())
        return root
    }

    private fun parseSyncData(dataJson: JSONObject): SyncClassData {
        // Profile
        val profileObj = dataJson.optJSONObject("profile")
        val profile = profileObj?.let { p ->
            ClassProfile(
                id = 1,
                className = p.optString("className", "KELAS XI-TKJ"),
                schoolName = p.optString("schoolName", "SMK Negeri 1"),
                academicYear = p.optString("academicYear", "2024 / 2025"),
                slogan = p.optString("slogan", "Teknik Komputer & Jaringan • Solid, Cerdas, Terkoneksi"),
                homeroomTeacher = p.optString("homeroomTeacher", "Budi Santoso, S.Kom., M.T."),
                homeroomTeacherSubject = p.optString("homeroomTeacherSubject", "Wali Kelas & Guru Produktif Jaringan"),
                classLeader = p.optString("classLeader", "Muhammad Rizaldi Pratama"),
                welcomeMessage = p.optString("welcomeMessage", "Selamat datang di website resmi KELAS XI-TKJ!"),
                instagramHandle = p.optString("instagramHandle", "@xi_tkj.official"),
                heroImageUrl = p.optString("heroImageUrl", ""),
                totalStudents = p.optInt("totalStudents", 22),
                maleStudents = p.optInt("maleStudents", 14),
                femaleStudents = p.optInt("femaleStudents", 8),
                weeklyCashTarget = p.optLong("weeklyCashTarget", 5000L)
            )
        }

        // Students
        val students = mutableListOf<StudentMember>()
        dataJson.optJSONArray("students")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                students.add(
                    StudentMember(
                        id = o.optLong("id", 0L),
                        absentNumber = o.optInt("absentNumber", i + 1),
                        name = o.optString("name", ""),
                        nickname = o.optString("nickname", ""),
                        gender = o.optString("gender", "L"),
                        role = o.optString("role", "Anggota"),
                        nisn = o.optString("nisn", ""),
                        quote = o.optString("quote", ""),
                        hobby = o.optString("hobby", ""),
                        dream = o.optString("dream", ""),
                        instagram = o.optString("instagram", ""),
                        photoUrl = o.optString("photoUrl", ""),
                        avatarColorIndex = o.optInt("avatarColorIndex", i % 6)
                    )
                )
            }
        }

        // Schedules
        val schedules = mutableListOf<ClassSchedule>()
        dataJson.optJSONArray("schedules")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                schedules.add(
                    ClassSchedule(
                        id = o.optLong("id", 0L),
                        dayOfWeek = o.optString("dayOfWeek", "Senin"),
                        orderNumber = o.optInt("orderNumber", i + 1),
                        subject = o.optString("subject", ""),
                        timeRange = o.optString("timeRange", ""),
                        teacher = o.optString("teacher", ""),
                        room = o.optString("room", "Ruang XI-TKJ")
                    )
                )
            }
        }

        // Pickets
        val pickets = mutableListOf<PicketSchedule>()
        dataJson.optJSONArray("pickets")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                pickets.add(
                    PicketSchedule(
                        id = o.optLong("id", 0L),
                        dayOfWeek = o.optString("dayOfWeek", "Senin"),
                        members = o.optString("members", ""),
                        taskDescription = o.optString("taskDescription", "")
                    )
                )
            }
        }

        // Transactions
        val transactions = mutableListOf<CashTransaction>()
        dataJson.optJSONArray("transactions")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                transactions.add(
                    CashTransaction(
                        id = o.optLong("id", 0L),
                        title = o.optString("title", ""),
                        amount = o.optLong("amount", 0L),
                        type = o.optString("type", "INCOME"),
                        date = o.optString("date", ""),
                        category = o.optString("category", ""),
                        recordedBy = o.optString("recordedBy", "Bendahara"),
                        notes = o.optString("notes", "")
                    )
                )
            }
        }

        // Gallery
        val gallery = mutableListOf<GalleryItem>()
        dataJson.optJSONArray("gallery")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                gallery.add(
                    GalleryItem(
                        id = o.optLong("id", 0L),
                        title = o.optString("title", ""),
                        category = o.optString("category", "Kegiatan"),
                        date = o.optString("date", ""),
                        imageUrl = o.optString("imageUrl", ""),
                        description = o.optString("description", ""),
                        likesCount = o.optInt("likesCount", 0),
                        isLiked = o.optBoolean("isLiked", false)
                    )
                )
            }
        }

        // Announcements
        val announcements = mutableListOf<Announcement>()
        dataJson.optJSONArray("announcements")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                announcements.add(
                    Announcement(
                        id = o.optLong("id", 0L),
                        title = o.optString("title", ""),
                        category = o.optString("category", "Pengumuman"),
                        content = o.optString("content", ""),
                        dueDate = o.optString("dueDate", ""),
                        author = o.optString("author", "Pengurus Kelas"),
                        datePosted = o.optString("datePosted", ""),
                        isCompleted = o.optBoolean("isCompleted", false)
                    )
                )
            }
        }

        // Spotify
        val spotifyTracks = mutableListOf<SpotifyTrackEntity>()
        dataJson.optJSONArray("spotifyTracks")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                spotifyTracks.add(
                    SpotifyTrackEntity(
                        id = o.optLong("id", 0L),
                        title = o.optString("title", ""),
                        artist = o.optString("artist", ""),
                        album = o.optString("album", ""),
                        durationText = o.optString("durationText", "0:30 (Preview)"),
                        coverUrl = o.optString("coverUrl", ""),
                        audioUrl = o.optString("audioUrl", ""),
                        spotifyUri = o.optString("spotifyUri", "")
                    )
                )
            }
        }

        return SyncClassData(
            profile = profile,
            students = students,
            schedules = schedules,
            pickets = pickets,
            transactions = transactions,
            gallery = gallery,
            announcements = announcements,
            spotifyTracks = spotifyTracks
        )
    }
}
