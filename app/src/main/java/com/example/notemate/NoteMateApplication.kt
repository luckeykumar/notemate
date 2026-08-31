package com.example.notemate

import android.app.Application
import com.example.notemate.data.AppDatabase
import com.example.notemate.data.repository.NoteMateRepository

class NoteMateApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: NoteMateRepository by lazy { NoteMateRepository(database) }
}
