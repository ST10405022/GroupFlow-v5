package com.example.groupflow.core.domain

data class Patient(
    override val id: String,
    override val name: String,
    val profileData: String,
    val appointments: List<Appointment> = emptyList()
) : User(id, name)