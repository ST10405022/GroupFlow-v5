package com.example.groupflow.ui.reviews

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.core.domain.Review
import com.example.groupflow.databinding.ActivityReviewsBinding
import com.example.groupflow.ui.NotificationsActivity
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.info.DoctorInfoActivity
import com.example.groupflow.ui.profile.UserProfileActivity
import java.time.LocalDateTime

class ReviewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                R.id.nav_home -> { // Home menu item
                    startActivity(Intent(this, MainActivity::class.java))
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

        // Sample data
        val sampleReviews = listOf(
            Review("1", "pat123", 5, "Excellent care!", LocalDateTime.now().minusDays(2)),
            Review("2", "pat456", 4, "Very friendly staff.", LocalDateTime.now().minusWeeks(1)),
            Review("3", "pat789", 3, "Average experience.", LocalDateTime.now().minusDays(5))
        )

        // RecyclerView setup
        binding.recyclerReviews.layoutManager = LinearLayoutManager(this)
        binding.recyclerReviews.adapter = ReviewAdapter(sampleReviews)
    }
}
