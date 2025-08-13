package com.example.groupflow.core.service

interface AuthenticationService {
    suspend fun register(email: String, password: String, displayName: String, role: String): Result<String>
    suspend fun login(email: String, password: String): Result<String>
    fun logout()
    fun currentUserId(): String?
}