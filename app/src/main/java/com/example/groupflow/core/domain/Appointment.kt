package com.example.groupflow.core.domain

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class Appointment(
    val id: String,
    val requestedDate: LocalDateTime,
    val status: Status = Status.PENDING,
    val patientId: String,
    val employeeId: String? = null
) {
    enum class Status { PENDING, APPROVED, DECLINED }
    init {
        require(requestedDate.isAfter(LocalDateTime.now())) {
            "Appointment must be in the future"
        }
    }
}