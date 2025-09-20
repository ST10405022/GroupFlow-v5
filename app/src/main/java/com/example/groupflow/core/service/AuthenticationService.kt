package com.example.groupflow.core.service

import com.example.groupflow.core.domain.User

interface AuthenticationService {
    suspend fun register(
        email: String,
        password: String,
        displayName: String,
        role: String,
    ): Result<String>

    suspend fun login(
        email: String,
        password: String,
    ): Result<String>

    fun logout()

    fun currentUserId(): String?

    suspend fun getCurrentUserProfile(): Result<User>
}
