package com.example.groupflow

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class GroupFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Force Light Mode for the entire app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
