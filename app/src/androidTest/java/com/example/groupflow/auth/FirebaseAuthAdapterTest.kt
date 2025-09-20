package com.example.groupflow.data.auth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.groupflow.core.service.AuthenticationService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseAuthAdapterTest {
    private lateinit var authService: AuthenticationService
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        authService = FirebaseAuthAdapter(context)
    }

    @Test
    fun testRegisterAndLogin() =
        runBlocking {
            val email = "testuser@example.com"
            val password = "password123"
            val displayName = "Test User"

            val registerResult = authService.register(email, password, displayName, "PATIENT")
            assertTrue(registerResult.isSuccess)

            val loginResult = authService.login(email, password)
            assertTrue(loginResult.isSuccess)

            val uid = loginResult.getOrNull()
            assertNotNull(uid)
        }

    @Test
    fun testLoginFailsWithWrongPassword() =
        runBlocking {
            val email = "wrongpass@example.com"
            val password = "password123"
            val displayName = "Wrong Pass User"

            authService.register(email, password, displayName, "EMPLOYEE")

            val loginResult = authService.login(email, "badpassword")
            assertTrue(loginResult.isFailure)
        }

    @Test
    fun testGetCurrentUserProfile() =
        runBlocking {
            val email = "profiletest@example.com"
            val password = "password123"
            val displayName = "Profile User"

            val registerResult = authService.register(email, password, displayName, "PATIENT")
            assertTrue(registerResult.isSuccess)

            val profileResult = authService.getCurrentUserProfile()
            assertTrue(profileResult.isSuccess)

            val user = profileResult.getOrNull()
            assertNotNull(user)
            assertEquals(displayName, user?.name)
        }
}
