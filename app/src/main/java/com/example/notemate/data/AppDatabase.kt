package com.example.notemate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notemate.data.dao.ActivityDao
import com.example.notemate.data.dao.BookingDao
import com.example.notemate.data.dao.ContactDao
import com.example.notemate.data.dao.UserDao
import com.example.notemate.data.model.ActivityEntity
import com.example.notemate.data.model.BookingEntity
import com.example.notemate.data.model.ContactEntity
import com.example.notemate.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        BookingEntity::class,
        ContactEntity::class,
        ActivityEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookingDao(): BookingDao
    abstract fun contactDao(): ContactDao
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notemate_database"
                )
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val userDao = db.userDao()
            val bookingDao = db.bookingDao()
            val contactDao = db.contactDao()
            val activityDao = db.activityDao()

            val demoUser = UserEntity(
                id = "u_demo_1",
                name = "Aarav Sharma",
                email = "aarav.sharma@vit.ac.in",
                phone = "+91 98765 43210",
                course = "B.Tech — CSE",
                password = "password123",
                createdAt = "2025-08-20T10:00:00Z"
            )
            userDao.insertUser(demoUser)

            activityDao.insertActivity(
                ActivityEntity(
                    id = "act_1",
                    userId = demoUser.id,
                    name = demoUser.name,
                    email = demoUser.email,
                    action = "register",
                    timestamp = "2025-08-20T10:00:00Z"
                )
            )

            bookingDao.insertBooking(
                BookingEntity(
                    id = "bk_101",
                    userId = demoUser.id,
                    course = "B.Tech — Bachelor of Technology",
                    name = "Aarav Sharma",
                    email = "aarav.sharma@vit.ac.in",
                    phone = "+91 98765 43210",
                    college = "VIT University Pune",
                    subject = "Operating Systems & Memory Management",
                    date = "2025-09-05",
                    type = "Assignment Solution",
                    requirements = "Need detailed thread scheduling algorithms diagram, memory paging explanation, and clean handwriting format with references.",
                    plan = "Pro Scholar",
                    files = "OS_Module_3_Questions.pdf (1.2 MB)",
                    status = "in_progress",
                    createdAt = "2025-08-25T14:30:00Z"
                )
            )

            bookingDao.insertBooking(
                BookingEntity(
                    id = "bk_102",
                    userId = demoUser.id,
                    course = "BCA — Bachelor of Computer Applications",
                    name = "Priya Patel",
                    email = "priya.patel@gmail.com",
                    phone = "+91 98220 11223",
                    college = "Symbiosis Institute",
                    subject = "Database Management Systems (DBMS)",
                    date = "2025-09-10",
                    type = "Lab Manual",
                    requirements = "Complete SQL query manual with ER diagrams and schema normalization up to 3NF.",
                    plan = "Starter Plan",
                    files = "DBMS_Lab_Syllabus.docx (850 KB)",
                    status = "done",
                    createdAt = "2025-08-22T09:15:00Z"
                )
            )

            contactDao.insertContact(
                ContactEntity(
                    id = "cnt_1",
                    userId = demoUser.id,
                    name = "Rohan Verma",
                    email = "rohan.v@du.ac.in",
                    course = "MBA — Master of Business Administration",
                    message = "Looking for assistance with a 45-page Marketing Strategy Research Report for Delhi University. Do you have specialized finance/marketing writers?",
                    status = "received",
                    createdAt = "2025-08-26T16:45:00Z"
                )
            )
        }
    }
}
