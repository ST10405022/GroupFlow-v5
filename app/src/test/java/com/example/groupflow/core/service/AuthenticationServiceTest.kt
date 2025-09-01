package com.example.groupflow.core.service

import com.example.groupflow.core.domain.Role
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class AuthenticationServiceTest {

    private lateinit var authService: AuthenticationService

    @Test
    fun `register new user succeeds`() = runTest {
        val result = authService.register("test@example.com", "password", "John Doe", Role.PATIENT.name)
        assertTrue(result.isSuccess)
        assertEquals("test@example.com", result.getOrNull())
        assertEquals("test@example.com", authService.currentUserId())
    }

    @Test
    fun `register existing user fails`() = runTest {
        authService.register("test@example.com", "password", "John Doe", Role.PATIENT.name)
        val result = authService.register("test@example.com", "password", "John Doe", Role.PATIENT.name)
        assertTrue(result.isFailure)
    }

    @Test
    fun `login with valid user succeeds`() = runTest {
        authService.register("test@example.com", "password", "John Doe", Role.PATIENT.name)
        authService.logout()

        val result = authService.login("test@example.com", "password")
        assertTrue(result.isSuccess)
        assertEquals("test@example.com", authService.currentUserId())
    }

    @Test
    fun `login with invalid user fails`() = runTest {
        val result = authService.login("notfound@example.com", "password")
        assertTrue(result.isFailure)
    }

    @Test
    fun `logout clears current user`() = runTest {
        authService.register("test@example.com", "password", "John Doe", Role.PATIENT.name)
        authService.logout()
        assertNull(authService.currentUserId())
    }

    @Test
    fun `getCurrentUserProfile returns logged in user`() = runTest {
        authService.register("test@example.com", "password", "John Doe", Role.PATIENT.name)
        val result = authService.getCurrentUserProfile()
        assertTrue(result.isSuccess)
        assertEquals("John Doe", result.getOrNull()?.name)
    }

    @Test
    fun `getCurrentUserProfile fails when no user logged in`() = runTest {
        val result = authService.getCurrentUserProfile()
        assertTrue(result.isFailure)
    }
}
