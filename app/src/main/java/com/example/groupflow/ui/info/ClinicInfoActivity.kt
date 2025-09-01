package com.example.groupflow.ui.info

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.core.domain.Role
import com.example.groupflow.core.domain.User
import com.example.groupflow.databinding.ActivityClinicInfoBinding
import com.example.groupflow.ui.NotificationsActivity
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.auth.SessionCreation
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.example.groupflow.ui.profile.UserProfileActivity

class ClinicInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClinicInfoBinding
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClinicInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBarClinic.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.topAppBarClinic.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    val intent = Intent(this, UserProfileActivity::class.java)
                    intent.putExtra("role", "Employee")
                    startActivity(intent)
                    true
                }
                R.id.menu_logout -> {
                    SessionCreation.logout(this)
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    true
                }
                else -> false
            }
        }

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
                } // Home menu item
                R.id.nav_appointments -> { // Appointments menu item
                    startActivity(Intent(this, AppointmentsActivity::class.java))
                    true
                }
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