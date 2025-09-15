package com.example.groupflow.ui.reviews

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.core.domain.Review
import com.example.groupflow.core.domain.Role
import com.example.groupflow.core.domain.User
import com.example.groupflow.data.review.FirebaseReviewRepo
import com.example.groupflow.databinding.ActivityLeaveReviewBinding
import com.example.groupflow.ui.notifications.NotificationsActivity
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.auth.SessionCreation
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.example.groupflow.ui.info.DoctorInfoActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class LeaveReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeaveReviewBinding
    private var currentUser: User? = null

    private fun showMessage(message: String){                       // (Android Developers, 2025)
        Toast.makeText(this@LeaveReviewActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLeaveReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user information
        currentUser = SessionCreation.getUser(this)

        binding.topAppBarReview.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Submit review button
        binding.btnSubmitReview.setOnClickListener {
            val rating = binding.ratingBar.rating.toInt()
            val comment = binding.editTextReview.text.toString()

            if (rating < 1 || rating > 5 || comment.isEmpty())
            {
                showMessage("Provide a comment and a rating")
                return@setOnClickListener
            }

            val review = Review(
                patientId = currentUser?.id,
                clinicId = null,
                rating = rating,
                comment = comment,
                createdDate = LocalDateTime.now()
            )

            CoroutineScope(Dispatchers.IO).launch {
                val result = FirebaseReviewRepo().addReview(review)
                withContext(Dispatchers.Main){
                    if (result.isSuccess){
                        showMessage("Review submitted")
                        finish()
                    }
                    else
                    {
                        showMessage("Failed to submit review")
                    }
                }
            }
        }

        // Top app bar menu item click listener
        binding.topAppBarReview.setOnMenuItemClickListener { menuItem ->
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
                } // Already on home screen
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
    }
}