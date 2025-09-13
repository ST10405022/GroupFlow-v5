package com.example.groupflow.ui.ultrascans

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.core.domain.Notification
import com.example.groupflow.core.domain.Role
import com.example.groupflow.core.domain.User
import com.example.groupflow.data.notification.FirebaseNotificationRepo
import com.example.groupflow.databinding.ActivityUploadUltrascanBinding
import com.example.groupflow.models.NotificationModel
import com.example.groupflow.ui.notifications.NotificationsActivity
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.auth.SessionCreation
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.example.groupflow.ui.info.DoctorInfoActivity
import com.example.groupflow.ui.patients.PatientSelectionActivity
import com.example.groupflow.ui.profile.UserProfileActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId

class UploadUltrascanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadUltrascanBinding
    private var fileUri: Uri? = null
    private var patientId: String? = null
    private var currentUser: User? = null

    // File picker
    private val pickFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                fileUri = result.data?.data
                fileUri?.let {
                    Toast.makeText(this, "Selected: $it", Toast.LENGTH_SHORT).show()
                    showPreview(it) // Show preview and approve button before uploading
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadUltrascanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = SessionCreation.getUser(this)
        patientId = intent.getStringExtra("patientId") ?: ""
        Log.d("UploadUltrascanActivity", "Received patientId: $patientId")

        // Check for patientId
        if (patientId.isNullOrEmpty()) {
            Toast.makeText(this, "No patient selected.", Toast.LENGTH_SHORT).show()
            Log.e("UploadUltrascanActivity", "Patient ID is null or empty")
            finish()
            return
        }
        Log.d("UploadUltrascanActivity", "Patient ID: $patientId")

        // Toolbar back icon
        binding.topAppBarUpload.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Toolbar menu
        binding.topAppBarUpload.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    true
                }
                R.id.menu_logout -> {
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    SessionCreation.logout(this)
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Select file button
        binding.uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    arrayOf("image/jpeg", "image/png", "application/pdf")
                )
            }
            pickFile.launch(intent)
        }

        // Bottom navigation
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
                R.id.nav_appointments -> {
                    startActivity(Intent(this, AppointmentsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, DoctorInfoActivity::class.java))
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // 🔥 Upload file to Firebase Storage
    private fun uploadFileToFirebase(uri: Uri) {
        isEmployee { allowed ->
            if (!allowed) {
                Toast.makeText(this, "Only employees can upload ultrascans.", Toast.LENGTH_SHORT)
                    .show()
                return@isEmployee
            }

            if (patientId?.isEmpty() == true) {
                Toast.makeText(this, "No patient selected.", Toast.LENGTH_SHORT).show()
                return@isEmployee
            }
            val storageRef = FirebaseStorage.getInstance().reference
            val fileName = "ultrascans/${UUID.randomUUID()}"
            val fileRef = storageRef.child(fileName)

            val uploadTask = fileRef.putFile(uri)
            uploadTask.addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveFileMetadata(downloadUrl.toString())
                    Toast.makeText(this, "File uploaded!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 💾 Save file metadata in Realtime Database and send notification
    private fun saveFileMetadata(fileUrl: String) {
        val ultrascansRef = FirebaseDatabase.getInstance().getReference("ultrascans")
        val uploadId = ultrascansRef.push().key ?: UUID.randomUUID().toString()
        val uploaderId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val description = "Uploaded by ${currentUser?.name} on ${System.currentTimeMillis()}"

        // Fetch patient name
        val patientRef = FirebaseDatabase.getInstance().getReference("users")
        patientRef.orderByChild("role").equalTo("PATIENT").get().addOnSuccessListener { snapshot ->
            var patientName = "UnknownPatient"

            // Find the patient with matching patientId
            for (child in snapshot.children) {
                if (child.child("id").getValue(String::class.java) == patientId) {
                    patientName = child.child("name").getValue(String::class.java) ?: "UnknownPatient"
                    break
                }
            }

            val patientNameSafe = patientName.replace(" ", "_")

            // Generate timestamped file name
            val sdf = SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault())
            val timestamp = sdf.format(Date())
            val fileName = "${patientNameSafe}_UltraScan_$timestamp"

            // Save metadata in Realtime Database
            val scanData = mapOf(
                "id" to uploadId,
                "fileUrl" to fileUrl,
                "uploadedAt" to System.currentTimeMillis(),
                "uploaderId" to uploaderId,
                "patientId" to patientId!!,
                "description" to description,
                "fileName" to fileName
            )

            ultrascansRef.child(uploadId).setValue(scanData)
                .addOnSuccessListener {
                    Log.d("UploadUltrascanActivity", "Scan metadata saved successfully")
                    Toast.makeText(this@UploadUltrascanActivity, "File uploaded and approved!", Toast.LENGTH_SHORT).show()

                    // Redirect back to hub
                    when (currentUser?.role) {
                        Role.EMPLOYEE -> startActivity(Intent(this@UploadUltrascanActivity, PatientSelectionActivity::class.java))
                        Role.PATIENT -> startActivity(Intent(this@UploadUltrascanActivity, MainActivity::class.java))
                        else -> Log.w("UploadUltrascanActivity", "Unknown role, staying on page")
                    }
                    finish()
                }
                .addOnFailureListener {
                    Log.e("UploadUltrascanActivity", "Failed to save scan metadata: ${it.message}")
                    Toast.makeText(this@UploadUltrascanActivity, "Failed to save scan metadata", Toast.LENGTH_SHORT).show()
                }

        }.addOnFailureListener {
            Log.e("UploadUltrascanActivity", "Failed to get patient info: ${it.message}")
            Toast.makeText(this@UploadUltrascanActivity, "Failed to retrieve patient info", Toast.LENGTH_SHORT).show()
        }
    }

    // 🔐 Check if the user is an employee
    private fun isEmployee(onResult: (Boolean) -> Unit) {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("role")

        dbRef.get().addOnSuccessListener { snapshot ->
            val role = snapshot.getValue(String::class.java)
            onResult(role == "EMPLOYEE")
        }.addOnFailureListener {
            onResult(false)
        }
    }

    // Show preview and approve button
    private fun showPreview(uri: Uri) {
        binding.previewLayout.visibility = View.VISIBLE
        binding.approveUploadButton.visibility = View.VISIBLE

        val mimeType = contentResolver.getType(uri) ?: ""
        Log.d("UploadUltrascanActivity", "Selected file MIME type: $mimeType")

        if (mimeType.startsWith("image/")) {
            binding.previewImage.visibility = View.VISIBLE
            binding.previewImage.setImageURI(uri)
            binding.previewFileName.visibility = View.GONE
        } else {
            binding.previewImage.visibility = View.GONE
            binding.previewFileName.visibility = View.VISIBLE
            binding.previewFileName.text = uri.lastPathSegment ?: "Unknown file"
        }

        binding.approveUploadButton.setOnClickListener {
            Log.d("UploadUltrascanActivity", "Approve button clicked, uploading file...")
            Toast.makeText(this, "Uploading file...", Toast.LENGTH_SHORT).show()
            uploadFileToFirebase(uri)
        }
    }
}
