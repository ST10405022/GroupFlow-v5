package com.example.groupflow.ui.hubs

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.R
import com.example.groupflow.core.domain.Role
import com.example.groupflow.core.domain.User
import com.example.groupflow.databinding.ActivityEmployeeHubBinding
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.auth.SessionCreation
import com.example.groupflow.ui.info.DoctorInfoActivity
import com.example.groupflow.ui.notifications.NotificationsActivity
import com.example.groupflow.ui.patients.PatientSelectionActivity
import com.example.groupflow.ui.profile.UserProfileActivity

class EmployeeHubActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeHubBinding
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityEmployeeHubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the logged-in user
        currentUser = SessionCreation.getUser(this) ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Upload Ultrascan Button (Employee only)
        binding.buttonUploadUltrascan.setOnClickListener {
            if (currentUser.role == Role.EMPLOYEE) {
                startActivity(Intent(this, PatientSelectionActivity::class.java))
            } else {
                Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show()
            }
        }

        // Back button behavior
        binding.topAppBarEmployee.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Profile button behavior
        binding.topAppBarEmployee.setOnMenuItemClickListener { menuItem ->
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
                    startActivity(
                        Intent(this, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        },
                    )
                    true
                }
                else -> false
            }
        }
        // Bottom navigation click listeners
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Already viewing home", Toast.LENGTH_SHORT).show()
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
