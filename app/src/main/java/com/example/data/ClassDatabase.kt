package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ClassDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ClassProfile::class,
        StudentMember::class,
        ClassSchedule::class,
        PicketSchedule::class,
        CashTransaction::class,
        GalleryItem::class,
        Announcement::class,
        SpotifyTrackEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ClassDatabase : RoomDatabase() {

    abstract fun classDao(): ClassDao

    companion object {
        @Volatile
        private var INSTANCE: ClassDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ClassDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClassDatabase::class.java,
                    "website_kelas_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(ClassDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class ClassDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.classDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(dao: ClassDao) {
            dao.insertOrUpdateProfile(InitialData.getDefaultProfile())
            dao.insertStudents(InitialData.getDefaultStudents())
            dao.insertSchedules(InitialData.getDefaultSchedules())
            dao.insertPickets(InitialData.getDefaultPickets())
            dao.insertTransactions(InitialData.getDefaultTransactions())
            dao.insertGalleryItems(InitialData.getDefaultGallery())
            dao.insertAnnouncements(InitialData.getDefaultAnnouncements())
            dao.insertSpotifyTracks(InitialData.getDefaultSpotifyTracks())
        }
    }
}
