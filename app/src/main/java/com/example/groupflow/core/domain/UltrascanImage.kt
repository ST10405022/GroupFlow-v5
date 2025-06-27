package com.example.groupflow.core.domain

import java.time.LocalDateTime

data class UltrascanImage(
    val id: String,
    val imageUrl: String,
    val uploadedDate: LocalDateTime,
    val patientId: String,
    val description: String? = null
)