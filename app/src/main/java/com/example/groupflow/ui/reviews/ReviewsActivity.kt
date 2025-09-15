package com.example.groupflow.ui.reviews

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.core.domain.Role
import com.example.groupflow.core.domain.User
import com.example.groupflow.data.review.FirebaseReviewRepo
import com.example.groupflow.databinding.ActivityReviewsBinding
import com.example.groupflow.ui.notifications.NotificationsActivity
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.auth.SessionCreation
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.example.groupflow.ui.info.DoctorInfoActivity
import com.example.groupflow.ui.profile.UserProfileActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReviewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewsBinding
    private var currentUser: User? = null

    private fun showMessage(message: String){                       // (Android Developers, 2025)
        Toast.makeText(this@ReviewsActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set the clinic id
        var clinicId = intent.getStringExtra("CLINIC_ID") ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val result = FirebaseReviewRepo().fetchReviewsForClinic(clinicId)
            withContext(Dispatchers.Main){
                if (result.isSuccess){
                    val reviews = result.getOrDefault(emptyList())
                    val adapter = ReviewAdapter(reviews)
                    binding.recyclerReviews.adapter = adapter
                }
                else
                {
                    showMessage("Error loading reviews")
                }
            }
        }

        binding = ActivityReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = SessionCreation.getUser(this)

        // Add Review button click listener
        binding.fabAddReview.setOnClickListener {
            startActivity(Intent(this, LeaveReviewActivity::class.java))
        }

        // Back button behavior
        binding.topAppBarReviews.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.topAppBarReviews.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
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

        // RecyclerView setup
        binding.recyclerReviews.layoutManager = LinearLayoutManager(this)
    }
}
