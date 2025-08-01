package com.example.groupflow.ui.hubs

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.databinding.ActivityEmployeeHubBinding
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.info.DoctorInfoActivity
import com.example.groupflow.ui.profile.UserProfileActivity
import com.example.groupflow.ui.ultrascans.UploadUltrascanActivity

class EmployeeHubActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeHubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeHubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example navigation for employees
        binding.buttonUploadUltrascan.setOnClickListener {
            startActivity(Intent(this, UploadUltrascanActivity::class.java))
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
                R.id.nav_appointments -> { // Notifications menu item
                    startActivity(Intent(this, AppointmentsActivity::class.java))
                    true
                }
                R.id.nav_profile -> { // Doctor Info menu item
                    startActivity(Intent(this, DoctorInfoActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
