package com.example.notemate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val userId: String? = null,
    val course: String,
    val name: String,
    val email: String,
    val phone: String,
    val college: String,
    val subject: String,
    val date: String,
    val type: String,
    val requirements: String,
    val plan: String? = null,
    val files: String? = null,
    val status: String = "pending", // "pending", "in_progress", "done"
    val createdAt: String
)
