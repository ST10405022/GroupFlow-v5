package com.example.groupflow.ui.ultrascans

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupflow.MainActivity
import com.example.groupflow.databinding.ActivityUltrascansBinding
import com.example.groupflow.models.UltrascanModel
import com.example.groupflow.ui.auth.SessionCreation
import com.google.firebase.database.*
import com.example.groupflow.R
import com.example.groupflow.core.domain.Role
import com.example.groupflow.core.domain.User
import com.example.groupflow.ui.notifications.NotificationsActivity
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.example.groupflow.ui.profile.UserProfileActivity

class UltrascansActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUltrascansBinding
    private val scans = mutableListOf<UltrascanModel>()
    private lateinit var adapter: UltrascanAdapter
    private var currentUser: User? = null

    // Holds the currently selected scan's file URL
    var currentFileUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUltrascansBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("UltrascansActivity", "onCreate called")

        // Get logged-in user from session
        currentUser = SessionCreation.getUser(this)

        // Back button in toolbar
        binding.topAppBarUltrascans.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Menu button in toolbar
        binding.topAppBarUltrascans.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    Log.i("UltrascansActivity", "Redirecting to profile")
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    true
                }
                R.id.menu_logout -> {
                    Log.i("UltrascansActivity", "Logging out user")
                    SessionCreation.logout(this)
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Bottom nav
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    when (currentUser?.role) {
                        Role.EMPLOYEE -> {
                            Log.d("UltrascansActivity", "Redirecting EMPLOYEE to hub")
                            startActivity(Intent(this, EmployeeHubActivity::class.java))
                        }
                        Role.PATIENT -> {
                            Log.d("UltrascansActivity", "Redirecting PATIENT to home")
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        else -> {
                            Log.e("UltrascansActivity", "Unknown or null role")
                            Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
                R.id.nav_appointments -> {
                    startActivity(Intent(this, AppointmentsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // RecyclerView + Adapter setup
        adapter = UltrascanAdapter(this, scans) { selectedUrl ->
            currentFileUrl = selectedUrl
            Toast.makeText(this, "Selected scan updated", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerUltrascans.layoutManager = LinearLayoutManager(this)
        binding.recyclerUltrascans.adapter = adapter

        // Initial visibility
        updateEmptyState()

        // Fetch scans from Firebase
        fetchScansFromFirebase()
    }

    /**
     * Fetches scans from Firebase and updates the RecyclerView.
     */
    private fun fetchScansFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("ultrascans")
        val currentPatientId = currentUser?.id // make sure currentUser is set

        if (currentPatientId.isNullOrEmpty()) {
            Log.e("UltrascansActivity", "Current patient ID is null or empty")
            binding.textNoScans.visibility = View.VISIBLE
            binding.recyclerUltrascans.visibility = View.GONE
            return
        }

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scans.clear()
                Log.d("UltrascansActivity", "Fetching scans for patientId=$currentPatientId...")

                for (scanSnap in snapshot.children) {
                    val scan = scanSnap.getValue(UltrascanModel::class.java)
                    if (scan != null) {
                        if (scan.patientId == currentPatientId) {
                            scans.add(scan)
                            Log.d("UltrascansActivity", "Loaded scan: ${scan.fileName} (${scan.fileUrl})")
                        } else {
                            Log.d("UltrascansActivity", "Skipped scan for different patient: ${scan.patientId}")
                        }
                    } else {
                        Log.w("UltrascansActivity", "Null scan skipped for snapshot: $scanSnap")
                    }
                }

                if (scans.isEmpty()) {
                    Log.d("UltrascansActivity", "No scans available for this patient")
                    binding.textNoScans.visibility = View.VISIBLE
                    binding.recyclerUltrascans.visibility = View.GONE
                } else {
                    binding.textNoScans.visibility = View.GONE
                    binding.recyclerUltrascans.visibility = View.VISIBLE
                    adapter.notifyDataSetChanged()
                    currentFileUrl = scans.lastOrNull()?.fileUrl
                    Log.d("UltrascansActivity", "Last selected fileUrl: $currentFileUrl")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UltrascansActivity", "Firebase error: ${error.message}")
                Toast.makeText(
                    this@UltrascansActivity,
                    "Failed to load scans: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * Updates the visibility of the "No Scans" TextView based on the number of scans.
     * If there are no scans, the RecyclerView is hidden and the TextView is made visible..
     */
    private fun updateEmptyState() {
        if (scans.isEmpty()) {
            binding.recyclerUltrascans.visibility = View.GONE
            binding.textNoScans.visibility = View.VISIBLE
        } else {
            binding.recyclerUltrascans.visibility = View.VISIBLE
            binding.textNoScans.visibility = View.GONE
        }
    }
}
