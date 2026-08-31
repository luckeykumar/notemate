package com.example.notemate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notemate.data.model.ActivityEntity
import com.example.notemate.data.model.BookingEntity
import com.example.notemate.data.model.ContactEntity
import com.example.notemate.data.model.Course
import com.example.notemate.data.model.NoteMateData
import com.example.notemate.data.model.PlanItem
import com.example.notemate.data.model.UserEntity
import com.example.notemate.data.repository.NoteMateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class NoteMateViewModel(
    private val repository: NoteMateRepository
) : ViewModel() {

    // Auth & Session
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    // Preferences
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Feedback
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Dialogs & Navigation state
    private val _selectedCourseForBooking = MutableStateFlow<Course?>(null)
    val selectedCourseForBooking: StateFlow<Course?> = _selectedCourseForBooking.asStateFlow()

    private val _selectedBookingDetail = MutableStateFlow<BookingEntity?>(null)
    val selectedBookingDetail: StateFlow<BookingEntity?> = _selectedBookingDetail.asStateFlow()

    private val _selectedPlanForPayment = MutableStateFlow<PlanItem?>(null)
    val selectedPlanForPayment: StateFlow<PlanItem?> = _selectedPlanForPayment.asStateFlow()

    val showBookingDialog = MutableStateFlow(false)
    val showAuthDialog = MutableStateFlow(false)
    val showPaymentDialog = MutableStateFlow(false)
    val showAdminAuthDialog = MutableStateFlow(false)
    val showHelpDialog = MutableStateFlow(false)
    val showTermsDialog = MutableStateFlow(false)

    // Course filters & search
    val selectedCategory = MutableStateFlow("all")
    val courseSearchQuery = MutableStateFlow("")

    val filteredCourses: StateFlow<List<Course>> = combine(
        selectedCategory,
        courseSearchQuery
    ) { category, query ->
        NoteMateData.courses.filter { course ->
            val matchCategory = category == "all" || course.category.equals(category, ignoreCase = true)
            val matchQuery = query.isBlank() ||
                    course.title.contains(query, ignoreCase = true) ||
                    course.full.contains(query, ignoreCase = true) ||
                    course.tags.any { it.contains(query, ignoreCase = true) }
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteMateData.courses)

    // Data from Repository
    val allBookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allContacts: StateFlow<List<ContactEntity>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allActivities: StateFlow<List<ActivityEntity>> = repository.allActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userCount: StateFlow<Int> = repository.userCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bookingCount: StateFlow<Int> = repository.bookingCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val contactCount: StateFlow<Int> = repository.contactCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activityCount: StateFlow<Int> = repository.activityCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Bookings for active user (or all if demo)
    val myBookings: StateFlow<List<BookingEntity>> = combine(
        allBookings,
        _currentUser
    ) { bookings, user ->
        if (user != null) {
            bookings.filter { it.userId == user.id || it.email.equals(user.email, ignoreCase = true) }
        } else {
            bookings
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun selectCourseForBooking(course: Course?) {
        _selectedCourseForBooking.value = course
        showBookingDialog.value = true
    }

    fun selectBookingDetail(booking: BookingEntity?) {
        _selectedBookingDetail.value = booking
    }

    fun selectPlanForPayment(plan: PlanItem) {
        _selectedPlanForPayment.value = plan
        showPaymentDialog.value = true
    }

    fun register(name: String, email: String, phone: String, course: String, pass: String) {
        viewModelScope.launch {
            val result = repository.registerUser(name, email, phone, course, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                showAuthDialog.value = false
                showToast("Welcome to NoteMate, ${user.name.split(" ").firstOrNull() ?: user.name}! 🎉")
            }.onFailure { error ->
                showToast("Error: ${error.message}")
            }
        }
    }

    fun login(emailOrPhone: String, pass: String) {
        viewModelScope.launch {
            val result = repository.loginUser(emailOrPhone, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                showAuthDialog.value = false
                showToast("Welcome back, ${user.name.split(" ").firstOrNull() ?: user.name}! 👋")
            }.onFailure { error ->
                showToast("Login Failed: ${error.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                repository.logActivity(user.id, user.name, user.email, "logout")
            }
            _currentUser.value = null
            showToast("Logged out successfully.")
        }
    }

    fun submitBooking(
        course: String,
        name: String,
        email: String,
        phone: String,
        college: String,
        subject: String,
        date: String,
        type: String,
        requirements: String,
        plan: String?,
        filesSummary: String?
    ) {
        viewModelScope.launch {
            val booking = BookingEntity(
                id = "bk_" + System.currentTimeMillis().toString(36),
                userId = _currentUser.value?.id,
                course = course,
                name = name,
                email = email,
                phone = phone,
                college = college,
                subject = subject,
                date = date,
                type = type,
                requirements = requirements,
                plan = plan ?: "Standard",
                files = filesSummary,
                status = "pending",
                createdAt = java.time.Instant.now().toString()
            )
            repository.createBooking(booking)
            showBookingDialog.value = false
            showToast("Slot booked successfully! We will contact you soon. 📞")
        }
    }

    fun updateBookingStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, status)
            showToast("Status updated to ${status.replace('_', ' ').uppercase()}")
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            repository.deleteBooking(bookingId)
            showToast("Booking removed.")
        }
    }

    fun submitContact(name: String, email: String, course: String, message: String) {
        viewModelScope.launch {
            val contact = ContactEntity(
                id = "cnt_" + UUID.randomUUID().toString().take(8),
                userId = _currentUser.value?.id,
                name = name,
                email = email,
                course = course,
                message = message,
                status = "received",
                createdAt = java.time.Instant.now().toString()
            )
            repository.submitContact(contact)
            showToast("Message sent! Our academic coordinator will contact you within 24 hours. 📩")
        }
    }

    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            repository.deleteContact(contactId)
            showToast("Inquiry cleared.")
        }
    }

    fun verifyAdminPassword(password: String): Boolean {
        return if (password == "notemate@2025") {
            _isAdminLoggedIn.value = true
            showAdminAuthDialog.value = false
            showToast("Admin access granted! 🛡️")
            true
        } else {
            showToast("Incorrect admin password.")
            false
        }
    }

    fun adminLogout() {
        _isAdminLoggedIn.value = false
        showToast("Admin session ended.")
    }

    fun exportCsvData(type: String): String {
        return when (type) {
            "bookings" -> {
                val header = "ID,Name,Email,Phone,Course,Subject,Type,Plan,Deadline,Status,Created\n"
                val rows = allBookings.value.joinToString("\n") { b ->
                    "\"${b.id}\",\"${b.name}\",\"${b.email}\",\"${b.phone}\",\"${b.course}\",\"${b.subject}\",\"${b.type}\",\"${b.plan}\",\"${b.date}\",\"${b.status}\",\"${b.createdAt}\""
                }
                header + rows
            }
            "users" -> {
                val header = "ID,Name,Email,Phone,Course,Created\n"
                val rows = allUsers.value.joinToString("\n") { u ->
                    "\"${u.id}\",\"${u.name}\",\"${u.email}\",\"${u.phone}\",\"${u.course}\",\"${u.createdAt}\""
                }
                header + rows
            }
            "contacts" -> {
                val header = "ID,Name,Email,Course,Message,Status,Created\n"
                val rows = allContacts.value.joinToString("\n") { c ->
                    "\"${c.id}\",\"${c.name}\",\"${c.email}\",\"${c.course}\",\"${c.message.replace("\"", "\"\"")}\",\"${c.status}\",\"${c.createdAt}\""
                }
                header + rows
            }
            else -> {
                val header = "ID,User,Email,Action,Timestamp\n"
                val rows = allActivities.value.joinToString("\n") { a ->
                    "\"${a.id}\",\"${a.name}\",\"${a.email}\",\"${a.action}\",\"${a.timestamp}\""
                }
                header + rows
            }
        }
    }
}

class NoteMateViewModelFactory(
    private val repository: NoteMateRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteMateViewModel::class.java)) {
            return NoteMateViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
