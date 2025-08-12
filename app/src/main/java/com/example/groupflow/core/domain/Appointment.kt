package com.example.groupflow.core.domain

import java.time.LocalDateTime

data class Appointment(
    val id: String = "",
    val requestedDate: LocalDateTime = LocalDateTime.now(),
    val status: Status = Status.PENDING,
    val reason: String = "",
    val patientId: String = "",
    val employeeId: String = ""
) {
    enum class Status { PENDING, APPROVED, DECLINED }
    init {
        require(requestedDate.isAfter(LocalDateTime.now())) {
            "Appointment must be in the future"
        }
    }
}