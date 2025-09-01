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
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.profile.UserProfileActivity

class PatientSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientSelectionBinding
    private val patients = mutableListOf<UserModel>()
    private lateinit var adapter: PatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.recyclerPatients.layoutManager = LinearLayoutManager(this)
        adapter = PatientAdapter(patients) { selectedPatient ->
            // When patient clicked, open UploadUltrascanActivity with patientId
            val intent = Intent(this, UploadUltrascanActivity::class.java)
            intent.putExtra("patientId", selectedPatient.id)
            startActivity(intent)
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
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
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
