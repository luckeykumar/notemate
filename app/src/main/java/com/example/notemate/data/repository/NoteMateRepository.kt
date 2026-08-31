package com.example.notemate.data.repository

import com.example.notemate.data.AppDatabase
import com.example.notemate.data.model.ActivityEntity
import com.example.notemate.data.model.BookingEntity
import com.example.notemate.data.model.ContactEntity
import com.example.notemate.data.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class NoteMateRepository(private val database: AppDatabase) {
    private val userDao = database.userDao()
    private val bookingDao = database.bookingDao()
    private val contactDao = database.contactDao()
    private val activityDao = database.activityDao()

    // Users
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val userCount: Flow<Int> = userDao.getUserCount()

    suspend fun registerUser(name: String, email: String, phone: String, course: String, password: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            val existingEmail = userDao.findByEmail(email)
            if (existingEmail != null) {
                return@withContext Result.failure(Exception("Email is already registered."))
            }
            if (phone.isNotBlank()) {
                val existingPhone = userDao.findByPhone(phone)
                if (existingPhone != null) {
                    return@withContext Result.failure(Exception("Phone number is already registered."))
                }
            }
            val user = UserEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                email = email,
                phone = phone,
                course = course,
                password = password,
                createdAt = java.time.Instant.now().toString()
            )
            userDao.insertUser(user)
            logActivity(user.id, user.name, user.email, "register")
            Result.success(user)
        }

    suspend fun loginUser(emailOrPhone: String, password: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            val user = if (emailOrPhone.contains("@")) {
                userDao.findByEmail(emailOrPhone)
            } else {
                userDao.findByPhone(emailOrPhone) ?: userDao.findByEmail(emailOrPhone)
            }
            if (user == null) {
                return@withContext Result.failure(Exception("No account found with this identifier."))
            }
            if (user.password.isNotBlank() && user.password != password) {
                return@withContext Result.failure(Exception("Incorrect password."))
            }
            logActivity(user.id, user.name, user.email, "login")
            Result.success(user)
        }

    // Bookings
    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()
    val bookingCount: Flow<Int> = bookingDao.getBookingCount()

    fun getBookingsForUser(email: String, userId: String): Flow<List<BookingEntity>> =
        bookingDao.getBookingsForUser(email, userId)

    suspend fun createBooking(booking: BookingEntity): Result<BookingEntity> =
        withContext(Dispatchers.IO) {
            bookingDao.insertBooking(booking)
            Result.success(booking)
        }

    suspend fun updateBookingStatus(id: String, status: String) =
        withContext(Dispatchers.IO) {
            bookingDao.updateStatus(id, status)
        }

    suspend fun deleteBooking(id: String) =
        withContext(Dispatchers.IO) {
            bookingDao.deleteBooking(id)
        }

    // Contacts
    val allContacts: Flow<List<ContactEntity>> = contactDao.getAllContacts()
    val contactCount: Flow<Int> = contactDao.getContactCount()

    suspend fun submitContact(contact: ContactEntity): Result<ContactEntity> =
        withContext(Dispatchers.IO) {
            contactDao.insertContact(contact)
            Result.success(contact)
        }

    suspend fun deleteContact(id: String) =
        withContext(Dispatchers.IO) {
            contactDao.deleteContact(id)
        }

    // Activities
    val allActivities: Flow<List<ActivityEntity>> = activityDao.getAllActivities()
    val activityCount: Flow<Int> = activityDao.getActivityCount()

    suspend fun logActivity(userId: String, name: String, email: String, action: String) =
        withContext(Dispatchers.IO) {
            activityDao.insertActivity(
                ActivityEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = name,
                    email = email,
                    action = action,
                    timestamp = java.time.Instant.now().toString()
                )
            )
        }
}
