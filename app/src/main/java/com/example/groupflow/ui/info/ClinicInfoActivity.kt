package com.example.groupflow.ui.info

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.R
import com.example.groupflow.databinding.ActivityClinicInfoBinding
import com.example.groupflow.ui.profile.UserProfileActivity

class ClinicInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClinicInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clinic_info)
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
                else -> false
            }
        }
    }
}