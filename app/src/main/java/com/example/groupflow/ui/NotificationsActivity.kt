package com.example.groupflow.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.databinding.ActivityNotificationsBinding
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.profile.UserProfileActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.info.DoctorInfoActivity

class NotificationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar back icon
        binding.topAppBarNotifications.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.topAppBarNotifications.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    true
                }

                R.id.menu_logout -> {
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        // Highlight correct nav item
        binding.bottomNav.selectedItemId = R.id.nav_notifications

        // Bottom navigation click listeners
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { // Home menu item
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_appointments -> { // Notifications menu item
                    startActivity(Intent(this, AppointmentsActivity::class.java))
                    true
                }
                R.id.nav_profile -> { // Doctor Info menu item
                    startActivity(Intent(this, DoctorInfoActivity::class.java))
                    true
                }
                R.id.nav_notifications -> { // Notifications menu item
                    Toast.makeText(this, "Already viewing notifications",
                        Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}