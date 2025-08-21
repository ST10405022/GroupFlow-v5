package com.example.groupflow.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.groupflow.MainActivity
import com.example.groupflow.core.domain.Role
import com.example.groupflow.data.AppDatabase
import com.example.groupflow.databinding.ActivityLoginBinding
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.google.firebase.crashlytics.internal.common.AppData
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private fun showMessage(message: String){
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle login logic
        binding.buttonLogin.setOnClickListener {
                                                                        // Login logic implementation
            val emailAddress = binding.editTextEmail.text.toString().trim()    // input username
            val password = binding.editTextPassword.text.toString().trim()     // input password

            if ((emailAddress == "") || (password == "")){                      // validate user input
                Toast.makeText(this,"Please enter email/password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {     // PTA 012
                try                                                     // check whether username exists
                {
                    val loggedIn = AppDatabase.authService.login(emailAddress, password)

                    if (loggedIn.isSuccess)                             // retrieve user profile
                    {
                        val profileResult = AppDatabase.authService.getCurrentUserProfile()
                        val currentUser = profileResult.getOrNull()

                                                                        // successful retrieval of profile
                        if (profileResult?.isSuccess == true && currentUser != null)
                        {
                                                                        // store user profile in session
                            SessionCreation.saveUser(this@LoginActivity, currentUser)

                                                                        // welcome the user
                            showMessage("Welcome ${currentUser.name}")

                            // redirect the user to their appropriate home screen
                            when (currentUser.role)
                            {
                                Role.PATIENT ->
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))

                                Role.EMPLOYEE ->
                                    startActivity(Intent(this@LoginActivity, EmployeeHubActivity::class.java))
                            }
                            finish()
                        }
                        else
                        {                            // show error message (unable to load profile)
                            showMessage("Failed to load user profile")
                        }
                    }
                    else
                    {                                // show error message (unsuccessful login)
                        showMessage("Login failed")
                    }
                }
                catch(e:Exception)
                {
                    showMessage("Unable to load user profile")
                }
            }
        }

        // Skip login and go to main activity
        binding.buttonSkipLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Go to RegisterActivity
        binding.registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}