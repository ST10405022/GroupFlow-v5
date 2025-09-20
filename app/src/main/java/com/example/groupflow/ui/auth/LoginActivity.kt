package com.example.groupflow.ui.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.groupflow.MainActivity
import com.example.groupflow.core.domain.Role
import com.example.groupflow.data.AppDatabase
import com.example.groupflow.databinding.ActivityLoginBinding
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private fun showMessage(message: String) { // (Android Developers, 2025)
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, notifications will work
                Log.d("LoginActivity", "Notification permission granted")
                showMessage("Notification permission granted")
            } else {
                // Permission denied
                Log.d("LoginActivity", "Notification permission denied")
                showMessage("Notification permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request notification permission
        askNotificationPermission()

        // Handle login logic
        binding.buttonLogin.setOnClickListener {
            // Login logic implementation
            val emailAddress =
                binding.editTextEmail.text
                    .toString()
                    .trim() // input username
            val password =
                binding.editTextPassword.text
                    .toString()
                    .trim() // input password

            if ((emailAddress == "") || (password == "")) { // validate user input
                Toast.makeText(this, "Please enter email/password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // (KotlinLang, 2025)
                try // check whether username exists
                    {
                        val loggedIn = AppDatabase.authService.login(emailAddress, password)

                        if (loggedIn.isSuccess) // retrieve user profile
                            {
                                val profileResult = AppDatabase.authService.getCurrentUserProfile()
                                val currentUser = profileResult.getOrNull()
                                // successful retrieval of profile
                                if (profileResult.isSuccess && currentUser != null) {
                                    // store user profile in a session (Roy)
                                    SessionCreation.saveUser(this@LoginActivity, currentUser)
                                    Log.d("LoginActivity", "User profile stored in session")
                                    // welcome the user
                                    showMessage("Welcome ${currentUser.name}")

                                    updateUserFcmToken(currentUser.id)

                                    // redirect the user to their appropriate home screen
                                    when (currentUser.role) {
                                        Role.PATIENT -> {
                                            Log.d("LoginActivity", "Starting MainActivity")
                                            startActivity(
                                                Intent(
                                                    this@LoginActivity,
                                                    MainActivity::class.java,
                                                ),
                                            )
                                        }
                                        Role.EMPLOYEE -> {
                                            Log.d("LoginActivity", "Starting EmployeeHubActivity")
                                            startActivity(
                                                Intent(
                                                    this@LoginActivity,
                                                    EmployeeHubActivity::class.java,
                                                ),
                                            )
                                        }
                                    }
                                    finish()
                                } else { // show error message (unable to load profile)
                                    showMessage("Failed to load user profile")
                                    Log.e("LoginActivity", "Failed to load user profile: ${profileResult.exceptionOrNull()?.message}")
                                }
                            } else { // show error message (unsuccessful login)
                            showMessage("Login failed")
                            Log.e("LoginActivity", "Login failed: ${loggedIn.exceptionOrNull()?.message}")
                        }
                    } catch (e: Exception) {
                    showMessage("Unable to load user profile")
                    Log.e("LoginActivity", "Failed to load user profile: ${e.message}")
                }
            }
        }

//        // Skip login and go to main activity
//        binding.buttonSkipLogin.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }

        // Go to RegisterActivity
        binding.registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            Log.d("LoginActivity", "Starting RegisterActivity")
            startActivity(intent)
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Already granted
                    Log.d("LoginActivity", "Notification permission already granted")
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show UI explaining why permission is needed
                    showMessage("Notification permission required")
                    Log.d("LoginActivity", "Notification permission required")
                }
                else -> {
                    // Directly ask for the permission
                    Log.d("LoginActivity", "Requesting notification permission")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    fun updateUserFcmToken(uid: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                FirebaseDatabase
                    .getInstance()
                    .getReference("users/$uid/fcmToken")
                    .setValue(token)
                    .addOnSuccessListener { Log.d("FCM", "Token updated for user $uid") }
                    .addOnFailureListener { Log.e("FCM", "Failed to update token: ${it.message}") }
            } else {
                Log.e("FCM", "Failed to get FCM token", task.exception)
            }
        }
    }
}

/**     Reference List
 *          KotlinLang. 2025. Coroutines. [Online]. Available at: https://kotlinlang.org/docs/coroutines-overview.html# [Accessed on 25 August 2025]
 *          Android Developers. 2025. Toast. [Online]. Available at: https://developer.android.com/reference/android/widget/Toast [Accessed on 25 August 2025]
 *  **/
