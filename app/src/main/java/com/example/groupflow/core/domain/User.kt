package com.example.groupflow.core.domain

abstract class User(
    open val id: String = "",
    open val name: String = "",
    open val email: String = "",
    open val role: Role = Role.PATIENT,
)

enum class Role { PATIENT, EMPLOYEE }
