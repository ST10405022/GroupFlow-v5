package com.example.groupflow

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import android.util.Log
import com.example.groupflow.data.AppDatabase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError

class GroupFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Force Light Mode for the entire app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        Log.d("GroupFlowApplication", "Firebase initialized")

        // initialize database with app context
        AppDatabase.init(this)

        // Check Firebase Realtime Database connection
        val database = FirebaseDatabase.getInstance()
        val connectedRef = database.getReference(".info/connected")
        connectedRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Log.i("GroupFlowApplication", "Connected to Firebase Realtime Database")
                } else {
                    Log.w("GroupFlowApplication", "Not connected to Firebase Realtime Database")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("GroupFlowApplication", "Firebase connection check cancelled: ${error.message}")
            }
        })
    }
}
