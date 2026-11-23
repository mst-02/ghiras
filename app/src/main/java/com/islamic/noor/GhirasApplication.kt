package com.islamic.noor

import android.app.Application
import com.islamic.noor.data.local.GhirasPreferences
import com.islamic.noor.data.repository.QuranRepository
import com.islamic.noor.data.repository.UserRepository

class GhirasApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize persistent preferences
        GhirasPreferences.init(this)
        // Initialize Quran JSON data assets
        QuranRepository.initialize(this)
        // Load persisted user profile
        UserRepository.instance.initFromPreferences()
    }
}
