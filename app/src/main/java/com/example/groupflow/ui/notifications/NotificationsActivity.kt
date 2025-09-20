package com.example.groupflow.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.core.domain.Role
import com.example.groupflow.core.domain.User
import com.example.groupflow.databinding.ActivityNotificationsBinding
import com.example.groupflow.models.NotificationModel
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.auth.SessionCreation
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.example.groupflow.ui.info.DoctorInfoActivity
import com.example.groupflow.ui.profile.UserProfileActivity
import com.google.firebase.database.*

class NotificationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsBinding

    // Notifications list (mutable for adapter)
    private val notifications = mutableListOf<NotificationModel>()

    // Adapter for RecyclerView
    private lateinit var adapter: NotificationsAdapter

    // Firebase database reference
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("notifications")

    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate view
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // NOW it's safe to get the user
        currentUser = SessionCreation.getUser(this)

        if (currentUser == null) {
            // Handle the case where the user is not logged in,
            // For now, just logging:
            Log.e("NotificationsActivity", "Current user is null, cannot proceed.")
            Toast
                .makeText(
                    this,
                    "Current user is null, cannot proceed.",
                    Toast.LENGTH_SHORT,
                ).show()
            finish() // Example: close activity if no user
            return
        }

        // Set up RecyclerView
        adapter = NotificationsAdapter(notifications)
        binding.recyclerNotifications.layoutManager = LinearLayoutManager(this)
        binding.recyclerNotifications.adapter = adapter

        // Fetch notifications from Firebase
        fetchNotifications()

        // Toolbar back icon
        binding.topAppBarNotifications.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Toolbar menu actions
        binding.topAppBarNotifications.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    true
                }
                R.id.menu_logout -> {
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    SessionCreation.logout(this)
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Highlight correct bottom nav item
        binding.bottomNav.selectedItemId = R.id.nav_notifications

        // Bottom navigation click listeners
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    when (currentUser?.role) {
                        Role.EMPLOYEE -> startActivity(Intent(this, EmployeeHubActivity::class.java))
                        Role.PATIENT -> startActivity(Intent(this, MainActivity::class.java))
                        else -> Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.nav_appointments -> {
                    startActivity(Intent(this, AppointmentsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, DoctorInfoActivity::class.java))
                    true
                }
                R.id.nav_notifications -> {
                    Toast.makeText(this, "Already viewing notifications", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Fetch notifications for the current user from Firebase.
     * Unread notifications are displayed at the top.
     */
    private fun fetchNotifications() {
        val userId = currentUser?.id ?: return

        // Listen for notifications in Firebase
        dbRef
            .orderByChild("recipientId")
            .equalTo(userId)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        notifications.clear()

                        for (snap in snapshot.children) {
                            val id = snap.child("id").getValue(String::class.java) ?: snap.key ?: continue
                            val message = snap.child("message").getValue(String::class.java) ?: ""
                            val recipientId = snap.child("recipientId").getValue(String::class.java) ?: ""
                            val millis = snap.child("timestamp").getValue(Long::class.java) ?: 0L
                            val read = snap.child("read").getValue(Boolean::class.java) ?: false
                            val relatedId = snap.child("relatedId").getValue(String::class.java) ?: ""
                            val type = snap.child("type").getValue(String::class.java) ?: ""

                            // Create notification model and add to list
                            notifications.add(NotificationModel(id, message, recipientId, millis, read, type, relatedId))
                        }

                        // Sort unread notifications first
                        notifications.sortWith(compareBy({ it.read }, { -it.timestamp }))

                        // Notify adapter
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast
                            .makeText(
                                this@NotificationsActivity,
                                "Failed to load notifications: ${error.message}",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                },
            )
    }
}
