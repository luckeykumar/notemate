package com.example.notemate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "login_activity")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val email: String,
    val action: String, // "login", "register", "logout"
    val timestamp: String
)
