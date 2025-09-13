package com.example.groupflow

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import android.util.Log
import com.example.groupflow.data.AppDatabase
import com.example.groupflow.ui.auth.SessionCreation
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.messaging.FirebaseMessaging

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

        // Fetch FCM token if user is logged in
        updateFcmTokenIfLoggedIn()

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

    private fun checkDatabaseConnection() {
        val database = FirebaseDatabase.getInstance()
        val connectedRef = database.getReference(".info/connected")
        connectedRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                Log.i("GroupFlowApplication", if (connected) "Connected to Firebase RTDB" else "Not connected")
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Log.e("GroupFlowApplication", "Firebase connection check cancelled: ${error.message}")
            }
        })
    }

    private fun updateFcmTokenIfLoggedIn() {
        val currentUser = SessionCreation.getUser(this)
        if (currentUser != null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("GroupFlowApplication", "FCM Token: $token")
                    val uid = currentUser.id
                    FirebaseDatabase.getInstance().getReference("users/$uid/fcmToken")
                        .setValue(token)
                        .addOnSuccessListener {
                            Log.d("FCM", "Token updated for user $uid")
                            Log.d("FCM", "Token: $token")
                        }
                        .addOnFailureListener { Log.e("FCM", "Failed to update token: ${it.message}") }
                } else {
                    checkDatabaseConnection()
                    Log.e("FCM", "Fetching FCM token failed", task.exception)
                }
            }
        }
    }
}
