package com.example.groupflow.ui.ultrascans

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import com.example.groupflow.databinding.ActivityUploadUltrascanBinding
import com.example.groupflow.ui.NotificationsActivity
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.auth.LoginActivity
import com.example.groupflow.ui.info.DoctorInfoActivity
import com.example.groupflow.ui.profile.UserProfileActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UploadUltrascanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadUltrascanBinding
    private var fileUri: Uri? = null
    private lateinit var patientId: String

    // File picker
    private val pickFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                fileUri = result.data?.data
                fileUri?.let {
                    Toast.makeText(this, "Selected: $it", Toast.LENGTH_SHORT).show()
                    uploadFileToFirebase(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadUltrascanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patientId = intent.getStringExtra("PATIENT_ID") ?: ""

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
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // 📌 Select Image/PDF button
        binding.uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*" // allow all
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "application/pdf"))
            }
            pickFile.launch(intent)
        }

        // Bottom navigation
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
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

            if (patientId.isEmpty()) {
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

    // 💾 Save file metadata in Realtime Database
    private fun saveFileMetadata(fileUrl: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("ultrascans")
        val uploadId = dbRef.push().key ?: UUID.randomUUID().toString()
        val uploaderId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val patientId = patientId

        val scanData = mapOf(
            "id" to uploadId,
            "fileUrl" to fileUrl,
            "uploadedAt" to System.currentTimeMillis(),
            "uploaderId" to uploaderId,
            "patientId" to patientId
        )

        dbRef.child(uploadId).setValue(scanData)
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
}
