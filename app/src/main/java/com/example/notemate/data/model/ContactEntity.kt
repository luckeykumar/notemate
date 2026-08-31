package com.example.notemate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val userId: String? = null,
    val name: String,
    val email: String,
    val course: String,
    val message: String,
    val status: String = "received",
    val createdAt: String
)
