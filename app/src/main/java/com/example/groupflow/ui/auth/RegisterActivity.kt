package com.example.groupflow.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupflow.databinding.ActivityRegisterBinding
import com.example.groupflow.MainActivity
import com.example.groupflow.R
import android.widget.Spinner

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up click listener for the register button
        binding.buttonRegister.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextRegisterEmail.text.toString()
            val password = binding.editTextRegisterPassword.text.toString()
            val selectedRole = findViewById<Spinner>(R.id.spinnerRole).selectedItem.toString()

            if (name.isNotBlank() && email.isNotBlank()
                && password.length >= 8 && selectedRole.isNotBlank()) {
                // Simulate successful registration
                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please fill in all fields with valid input", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
