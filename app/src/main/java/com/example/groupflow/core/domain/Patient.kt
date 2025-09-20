package com.example.groupflow.core.domain

data class Patient(
    override val id: String = "",
    override val name: String = "",
    override val email: String = "",
    override val role: Role = Role.PATIENT,
    val profileData: String = "",
    val appointments: List<Appointment> = emptyList(),
) : User(id, name, email, role)
