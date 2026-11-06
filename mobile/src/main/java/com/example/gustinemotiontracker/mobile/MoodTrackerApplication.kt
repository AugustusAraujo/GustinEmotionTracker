package com.example.gustinemotiontracker.mobile

import android.app.Application
import com.example.gustinemotiontracker.mobile.data.AppDatabase
import com.example.gustinemotiontracker.mobile.data.MoodRepository

class MoodTrackerApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.get(this) }

    val repository: MoodRepository by lazy { MoodRepository(database.moodEntryDao()) }
}
