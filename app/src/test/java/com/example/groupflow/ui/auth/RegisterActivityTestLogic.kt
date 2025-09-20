package com.example.groupflow.ui.auth

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RegisterActivityTestLogic {
    private lateinit var registerActivity: RegisterActivity

    @Before
    fun setUp() {
        registerActivity = RegisterActivity()
    }

    @Test
    fun validInput_returnsTrue_forValidFields() {
        val result =
            registerActivity
                .run { invokeValidInput("John Doe", "john@example.com", "PATIENT", "password123") }
        assertTrue(result)
    }

    @Test
    fun validInput_returnsFalse_forEmptyFields() {
        val result =
            registerActivity
                .run { invokeValidInput("", "john@example.com", "PATIENT", "password123") }
        assertFalse(result)
    }

    @Test
    fun validInput_returnsFalse_forInvalidEmail() {
        val result =
            registerActivity
                .run { invokeValidInput("John Doe", "not-an-email", "PATIENT", "password123") }
        assertFalse(result)
    }

    @Test
    fun validInput_returnsFalse_forShortPassword() {
        val result =
            registerActivity
                .run { invokeValidInput("John Doe", "john@example.com", "PATIENT", "123") }
        assertFalse(result)
    }

    @Test
    fun validInput_returnsFalse_forEmptyRole() {
        val result =
            registerActivity
                .run { invokeValidInput("John Doe", "john@example.com", "", "password123") }
        assertFalse(result)
    }

    // Helper to access private method
    private fun RegisterActivity.invokeValidInput(
        name: String,
        email: String,
        role: String,
        password: String,
    ): Boolean {
        val method =
            RegisterActivity::class.java.getDeclaredMethod(
                "validInput",
                String::class.java,
                String::class.java,
                String::class.java,
                String::class.java,
            )
        method.isAccessible = true
        return method.invoke(this, name, email, role, password) as Boolean
    }
    // (Android Developers, n.d.)
}
