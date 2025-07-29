package com.example.groupflow.core.service

import com.example.groupflow.core.domain.User

interface AuthenticationService {
    fun login(username: String, password: String, role: String): User?
    fun logout(userId: String)
}