package com.example.groupflow.core.domain

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class Review(
    val id: String,
    val patientId: String,
    val rating: Int,
    val comment: String,
    val createdDate: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(rating in 1..5) { "Rating must be between 1 and 5" }
    }
}