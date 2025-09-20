package com.example.groupflow.core.domain

import java.time.LocalDateTime

data class Review(
    val id: String = "",
    val patientId: String? = "",
    val clinicId: String? = "",
    val rating: Int = 0,
    val comment: String = "",
    val createdDate: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(rating in 1..5) { "Rating must be between 1 and 5" }
    }
}
