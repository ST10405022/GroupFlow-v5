package com.example.groupflow.ui.auth

import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private fun validInput(name: String, email: String, role: String, password: String): Boolean
    {
        return  name.isNotBlank() &&
                email.isNotBlank() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length >= 8 &&
                role.isNotBlank()   //PTA 012
    }

    private fun userRegistration(name: String, email: String, role: String, password: String)
    {
        lifecycleScope.launch {
            try
            {
                val registered = AppDatabase.authService.register(email, password, name, role)

                if (registered.isSuccess)
                {
                    val profileResult = AppDatabase.authService.getCurrentUserProfile()
                    val currentUser = profileResult.getOrNull()

                    if (profileResult?.isSuccess == true && currentUser != null)
                    {
                        SessionCreation.saveUser(this@RegisterActivity, currentUser) // welcome the user
                        showMessage("Welcome ${currentUser.name}")

                        // redirect the user to their appropriate home screen
                        when (currentUser.role)
                        {
                            Role.PATIENT ->
                                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))

                            Role.EMPLOYEE ->
                                startActivity(Intent(this@RegisterActivity, EmployeeHubActivity::class.java))
                        }
                        finish()
                    }
                    else
                    {                            // show error message (unable to load profile)
                        showMessage("Failed to register and load user profile")
                    }
                }
                else
                {                                // show error message (unsuccessful login)
                    showMessage("User registration failed")
                }
            }
            catch (e: Exception)
            {
                showMessage("Unable to register user profile: ${e.message}")
            }
        }
    }

    private fun showMessage(message: String){
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
}
