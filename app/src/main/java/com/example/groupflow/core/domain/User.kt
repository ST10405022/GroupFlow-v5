package com.example.groupflow.core.domain

abstract class User(
    open val id: String = "",
    open val name: String = "",
)
enum class Role { PATIENT, EMPLOYEE }