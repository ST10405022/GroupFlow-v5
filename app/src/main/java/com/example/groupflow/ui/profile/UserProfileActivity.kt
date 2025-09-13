package com.example.groupflow.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.core.domain.Role
import com.example.groupflow.core.domain.User
import com.example.groupflow.databinding.ActivityUserProfileBinding
import com.example.groupflow.ui.notifications.NotificationsActivity
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.auth.SessionCreation
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.example.groupflow.ui.info.DoctorInfoActivity
import com.google.android.material.navigation.NavigationBarView

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get logged-in user from session
        currentUser = SessionCreation.getUser(this)

        if (currentUser == null) {
            // If no user session found, force logout
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            SessionCreation.logout(this)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }

        // Toolbar back icon
        binding.topAppBarProfile.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Toolbar menu actions
        binding.topAppBarProfile.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    Toast.makeText(this, "Already viewing profile", Toast.LENGTH_SHORT).show()
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

        // Display user data
        binding.textUserName.text = currentUser!!.name
        binding.textEmail.text = currentUser!!.email
        binding.textUserRole.text = currentUser!!.role.name // "PATIENT" or "EMPLOYEE"

        // Bottom navigation actions
        binding.bottomNav.setOnItemSelectedListener(navListener)
    }

    // Handle bottom nav actions
    private val navListener = NavigationBarView.OnItemSelectedListener { item ->
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
                startActivity(Intent(this, NotificationsActivity::class.java))
                true
            }
            else -> false
        }
    }
}
