package com.example.groupflow

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.data.AppDatabase
import com.example.groupflow.databinding.ActivityMainBinding
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.info.ClinicInfoActivity
import com.example.groupflow.ui.NotificationsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.auth.SessionCreation
import com.example.groupflow.ui.info.DoctorInfoActivity
import com.example.groupflow.ui.profile.UserProfileActivity
import com.example.groupflow.ui.reviews.ReviewsActivity
import com.example.groupflow.ui.ultrascans.UltrascansActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private fun showMessage(message: String){
                                                    // show message function declaration
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        // Card click listeners for Appointments
        binding.cardAppointments.setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
        }

        // Card click listeners for Ultrascans
        binding.cardUltrascans.setOnClickListener {
            startActivity(Intent(this, UltrascansActivity::class.java))
        }

        // Card click listeners for Reviews
        binding.cardReviews.setOnClickListener {
            startActivity(Intent(this, ReviewsActivity::class.java))
        }

        // Card click listeners for Clinic Info
        binding.cardClinicInfo.setOnClickListener {
            startActivity(Intent(this, ClinicInfoActivity::class.java))
        }

        // Highlight correct nav item
        binding.bottomNav.selectedItemId = R.id.nav_home

        // Bottom navigation click listeners
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showMessage("Already viewing home")
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

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu Menu to be inflated.
     * @return True to display the menu, false otherwise.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    /**
     * Handle menu item selection.
     * @param item Selected menu item.
     * @return True if the selection is handled, false otherwise.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {       // PTA 012
        return when (item.itemId) {
            R.id.menu_logout -> {
                                                                        // log out authentication service
                AppDatabase.authService
                                                                        // clear the active session
                SessionCreation.logout(this)
                                                                        // display logout message
                showMessage("Logged out")
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }
            R.id.menu_profile -> { // Profile menu item
                startActivity(Intent(this, UserProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
