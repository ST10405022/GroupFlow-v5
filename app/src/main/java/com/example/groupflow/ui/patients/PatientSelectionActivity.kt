package com.example.groupflow.ui.patients

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupflow.R
import com.example.groupflow.databinding.ActivityPatientSelectionBinding
import com.example.groupflow.models.UserModel
import com.example.groupflow.ui.ultrascans.UploadUltrascanActivity
import com.google.firebase.database.*
import android.util.Log
import android.view.View
import com.example.groupflow.MainActivity
import com.example.groupflow.core.domain.User
import com.example.groupflow.core.domain.Role
import com.example.groupflow.ui.NotificationsActivity
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.auth.SessionCreation
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.example.groupflow.ui.profile.UserProfileActivity

class PatientSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientSelectionBinding
    private val patients = mutableListOf<UserModel>()
    private lateinit var adapter: PatientAdapter
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the logged-in user
        currentUser = SessionCreation.getUser(this) ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Setup RecyclerView
        binding.recyclerPatients.layoutManager = LinearLayoutManager(this)
        if (currentUser.role == Role.EMPLOYEE) {
            adapter = PatientAdapter(patients) { selectedPatient ->
                // When patient clicked, open UploadUltrascanActivity with patientId
                val intent = Intent(this, UploadUltrascanActivity::class.java)
                intent.putExtra("patientId", selectedPatient.id)
                Log.d("PatientSelectionActivity",
                    "Starting UploadUltrascanActivity with patientId: ${selectedPatient.id}")
                startActivity(intent)
            }
        } else {
            SessionCreation.logout(this)
            Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
        binding.recyclerPatients.adapter = adapter

        binding.topAppBarPatients.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.topAppBarPatients.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    true
                }
                R.id.menu_logout -> {
                    SessionCreation.logout(this)
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    true
                }
                else -> false
            }
        }

        // Bottom navigation click listeners
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    when (currentUser.role) {
                        Role.EMPLOYEE -> startActivity(Intent(this, EmployeeHubActivity::class.java))
                        Role.PATIENT -> startActivity(Intent(this, MainActivity::class.java))
                    }
                    true
                }
                R.id.nav_appointments -> { // Notifications menu item
                    startActivity(Intent(this, AppointmentsActivity::class.java))
                    true
                }
                R.id.nav_profile -> { // Doctor Info menu item
                    Toast.makeText(this, "Already viewing doctor info",
                        Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_notifications -> { // Notifications menu item
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        fetchPatientsFromFirebase()
    }

    private fun fetchPatientsFromFirebase() {
        // Get a reference to the "users" node in Firebase Realtime Database
        val dbRef = FirebaseDatabase.getInstance().getReference("users")

        // Query the database for users where the "role" field equals "Patient"
        dbRef.orderByChild("role").equalTo("PATIENT")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Clear the current list of patients to avoid duplicates
                    patients.clear()

                    // Check if the snapshot contains any patients
                    if (!snapshot.exists()) {
                        // No patients found; show empty state text and hide RecyclerView
                        binding.textNoPatients.visibility = View.VISIBLE
                        binding.recyclerPatients.visibility = View.GONE
                        android.util.Log.d("PatientSelection", "No patients found in database")
                        return
                    }

                    // Loop through each child node (each patient) in the snapshot
                    for (userSnap in snapshot.children) {
                        // Convert the snapshot into a UserModel object
                        val user = userSnap.getValue(UserModel::class.java)
                        if (user != null) {
                            // Add the patient to the list
                            patients.add(user)
                            // Log loaded patient information for debugging
                            android.util.Log.d(
                                "PatientSelection",
                                "Loaded patient: ${user.name}, ${user.email}"
                            )
                        } else {
                            // Log a warning if a user snapshot could not be converted
                            android.util.Log.w(
                                "PatientSelection",
                                "Null user found for snapshot: $userSnap"
                            )
                        }
                    }

                    // Update the UI based on whether any patients were loaded
                    if (patients.isEmpty()) {
                        // Show empty state if no valid patients were loaded
                        binding.textNoPatients.visibility = View.VISIBLE
                        binding.recyclerPatients.visibility = View.GONE
                    } else {
                        // Show RecyclerView and hide empty state text
                        binding.textNoPatients.visibility = View.GONE
                        binding.recyclerPatients.visibility = View.VISIBLE
                        // Notify the adapter that data has changed so RecyclerView updates
                        adapter.notifyDataSetChanged()
                    }

                    // Log the total number of patients loaded
                    android.util.Log.d("PatientSelection", "Total patients loaded: ${patients.size}")
                }

                override fun onCancelled(error: DatabaseError) {
                    // Show error message in empty state TextView
                    binding.textNoPatients.visibility = View.VISIBLE
                    binding.textNoPatients.text = "Failed to load patients"
                    // Hide the RecyclerView since loading failed
                    binding.recyclerPatients.visibility = View.GONE
                    // Display a Toast message for immediate user feedback
                    Toast.makeText(
                        this@PatientSelectionActivity,
                        "Failed to load patients: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Log the Firebase error for debugging
                    android.util.Log.e("PatientSelection", "Firebase error: ${error.message}")
                }
            })
    }
}
