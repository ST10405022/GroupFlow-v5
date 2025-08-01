package com.example.groupflow.ui.hubs

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.databinding.ActivityEmployeeHubBinding
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

        binding.topAppBarEmployee.setNavigationOnClickListener {
            // Navigate to profile
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("role", "Employee")
            startActivity(intent)
        }
    }
}
