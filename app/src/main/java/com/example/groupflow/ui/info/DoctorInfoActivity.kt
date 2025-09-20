package com.example.groupflow.ui.info

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.core.domain.DoctorInfo
import com.example.groupflow.core.domain.Role
import com.example.groupflow.core.domain.User
import com.example.groupflow.databinding.ActivityDoctorInfoBinding
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.auth.SessionCreation
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.example.groupflow.ui.notifications.NotificationsActivity
import com.example.groupflow.ui.profile.UserProfileActivity

class DoctorInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorInfoBinding
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityDoctorInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = SessionCreation.getUser(this)

        setSupportActionBar(binding.topAppBarDoctorInfo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.topAppBarDoctorInfo.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.topAppBarDoctorInfo.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    // Handle profile menu item click
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

        // Highlight correct nav item
        binding.bottomNav.selectedItemId = R.id.nav_profile

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
                R.id.nav_appointments -> { // Notifications menu item
                    startActivity(Intent(this, AppointmentsActivity::class.java))
                    true
                }
                R.id.nav_profile -> { // Doctor Info menu item
                    Toast
                        .makeText(
                            this,
                            "Already viewing doctor info",
                            Toast.LENGTH_SHORT,
                        ).show()
                    true
                }
                R.id.nav_notifications -> { // Notifications menu item
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Example doctor data (replace with actual source later)
        val doctor =
            DoctorInfo(
                name = "Dr. Emily Smith",
                specialty = "Obstetrician & Gynaecologist",
                phoneNumber = "+27 82 123 4567",
                email = "dr.emily@clinic.co.za",
                clinicName = "GroupFlow Women’s Health Clinic",
            )

        displayDoctorInfo(doctor)
    }

    private fun displayDoctorInfo(doctor: DoctorInfo) {
        binding.textDoctorName.text = doctor.name
        binding.textDoctorSpecialty.text = doctor.specialty
        binding.textDoctorPhone.text = doctor.phoneNumber
        binding.textDoctorEmail.text = doctor.email
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
