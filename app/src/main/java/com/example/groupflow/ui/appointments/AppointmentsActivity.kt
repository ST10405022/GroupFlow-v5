package com.example.groupflow.ui.appointments

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.databinding.ActivityAppointmentsBinding
import com.example.groupflow.ui.NotificationsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.info.DoctorInfoActivity
import com.example.groupflow.ui.profile.UserProfileActivity

class AppointmentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppointmentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Add Appointment button
        binding.fabAddAppointment.setOnClickListener {
            startActivity(Intent(this, RequestAppointmentActivity::class.java))
        }

        // Set up the top app bar
        binding.topAppBarAppointments.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.topAppBarAppointments.setOnMenuItemClickListener { menuItem ->
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
        binding.bottomNav.selectedItemId = R.id.nav_appointments

        // Bottom navigation click listeners
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { // Home menu item
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_appointments -> {
                    Toast.makeText(this, "Already viewing appointments", Toast.LENGTH_SHORT).show()
                    true
                } // Notifications menu item

                R.id.nav_profile -> { // Doctor Info menu item
                    startActivity(Intent(this, DoctorInfoActivity::class.java))
                    true
                }
                R.id.nav_notifications -> { // Notifications menu item
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}