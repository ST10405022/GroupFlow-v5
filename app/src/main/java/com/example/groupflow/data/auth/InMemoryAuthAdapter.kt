package com.example.groupflow.data.auth

import com.example.groupflow.core.domain.Employee
import com.example.groupflow.core.domain.Patient
import com.example.groupflow.core.service.AuthenticationService
import java.util.*

class InMemoryAuthAdapter : AuthenticationService {
    private val users = mutableMapOf(
        "patient1" to Patient("p1", "Alice", "Profile..."),
        "employee1" to Employee("e1", "Dr. Varona", "Clinic owner")
    )

    override fun login(username: String, password: String, role: String) =
        users[username]

    override fun logout(userId: String) { /* no-op */ }
}
