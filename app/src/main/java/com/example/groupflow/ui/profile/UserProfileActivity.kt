package com.example.groupflow.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.databinding.ActivityUserProfileBinding
import com.example.groupflow.ui.NotificationsActivity
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.example.groupflow.ui.info.DoctorInfoActivity
import com.google.android.material.navigation.NavigationBarView

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding

    // Simulated user data
    // ( will be replaced with real user session or ViewModel in later sprints)
    private val userName = "John Doe"
    private val userEmail = "john@example.com"
    private val userRole = "Patient" // or "Employee"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar back icon
        binding.topAppBarProfile.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Toolbar menu (e.g., profile settings)
        binding.topAppBarProfile.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    Toast.makeText(this, "Already viewing profile", Toast.LENGTH_SHORT).show()
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

        // Set user data to TextViews
        binding.textUserName.text = userName
        binding.textEmail.text = userEmail
        binding.textUserRole.text = userRole

        // Bottom navigation actions
        binding.bottomNav.setOnItemSelectedListener(navListener)
    }

    // Handle bottom nav actions
    private val navListener = NavigationBarView.OnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                // Redirect to role-based hub
                if (userRole.equals("Employee", ignoreCase = true)) {
                    startActivity(Intent(this, EmployeeHubActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
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
