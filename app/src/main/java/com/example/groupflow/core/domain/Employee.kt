package com.example.groupflow.core.domain

data class Employee(
    override val id: String = "",
    override val name: String = "",
    override val email: String = "",
    override val role: Role = Role.EMPLOYEE,
    val profileData: String = "",
    val managesPatients: List<String> = emptyList(), // patient IDs
    val uploadsScans: List<UltrascanImage> = emptyList()
) : User(id, name, email, role)
