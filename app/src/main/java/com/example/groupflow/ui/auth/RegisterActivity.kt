package com.example.groupflow.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.databinding.ActivityRegisterBinding
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import android.widget.Spinner
import androidx.lifecycle.lifecycleScope
import com.example.groupflow.core.domain.Role
import com.example.groupflow.data.AppDatabase
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private fun validInput(name: String, email: String, role: String, password: String): Boolean
    {
        val validRole = try{
            Role.valueOf(role.uppercase())
            true
        }
        catch (e: IllegalArgumentException){
            false
        }
        return  name.isNotBlank() &&
                email.isNotBlank() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length >= 8 &&
                role.isNotBlank()   //PTA 012
    }

    private fun userRegistration(name: String, email: String, role: String, password: String)
    {
        lifecycleScope.launch {                                             // (KotlinLang, 2025)
            try
            {
                val registered = AppDatabase.authService.register(email, password, name, role)

                if (registered.isSuccess)
                {
                    val profileResult = AppDatabase.authService.getCurrentUserProfile()
                    val currentUser = profileResult.getOrNull()

                    if (profileResult.isSuccess && currentUser != null)
                    {
                        SessionCreation.saveUser(this@RegisterActivity, currentUser)
                        Log.d("RegisterActivity", "User registered and profile loaded successfully")
                        // welcome the user
                        showMessage("Welcome ${currentUser.name}")

                        // update the user's FCM token
                        updateUserFcmToken(currentUser.id)

                        // redirect the user to their appropriate home screen
                        when (currentUser.role)
                        {
                            Role.PATIENT -> {
                                Log.d("RegisterActivity", "User is a patient")
                                startActivity(
                                    Intent(
                                        this@RegisterActivity,
                                        MainActivity::class.java
                                    )
                                )
                            }
                            Role.EMPLOYEE -> {
                                Log.d("RegisterActivity", "User is an employee")
                                startActivity(
                                    Intent(
                                        this@RegisterActivity,
                                        EmployeeHubActivity::class.java
                                    )
                                )
                            }
                        }
                        finish()
                    }
                    else
                    {
                        // show error message (unable to load profile)
                        showMessage("Failed to register and load user profile")
                        Log.e("RegisterActivity", "Failed to load user profile")
                    }
                }
                else
                {
                    val exception = registered.exceptionOrNull()
                    if (exception != null)
                    {
                        // check if the exception message contains the specific error message
                        if (exception.message?.contains("email address is already in use",
                                ignoreCase = true) == true) {
                            // show error message (email already in use)
                            showMessage("This email address is already in use. Please log in or use a different email.")
                            Log.e("RegisterActivity", "Email address is already in use")
                        } else {
                            // show error message (user registration failed)
                            showMessage("User registration failed: ${exception.message}")
                        }
                        Log.e("RegisterActivity", "User registration failed", exception)
                    }
                    else { // show error message (unsuccessful login)
                        showMessage("User registration failed")
                        Log.e("RegisterActivity", "User registration failed")
                    }

                }
            }
            catch (e: Exception)
            {
                showMessage("Unable to register user profile: ${e.message}")
                Log.e("RegisterActivity", "Unable to register user profile", e)

                if (e is FirebaseAuthUserCollisionException)
                {
                    showMessage("This email address is already in use. Please log in or use a different email.")
                    Log.e("RegisterActivity", "Email address is already in use")
                }
            }
        }
    }

    private fun showMessage(message: String){                                                       // (Android Developers, 2025)
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up click listener for the register button
        binding.buttonRegister.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val email = binding.editTextRegisterEmail.text.toString().trim()
            val password = binding.editTextRegisterPassword.text.toString().trim()
            val selectedRole = findViewById<Spinner>(R.id.spinnerRole).selectedItem.toString().trim()

                                        // ensure that all the fields meet the registration criteria
            if (validInput(name, email, selectedRole, password))
            {

            // Registration implementation
                userRegistration(name, email, selectedRole, password)
            }
            else
            {
                showMessage("Please fill in all fields with valid input")
            }
        }

        binding.registerTitle.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun updateUserFcmToken(uid: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                FirebaseDatabase.getInstance().getReference("users/$uid/fcmToken")
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
