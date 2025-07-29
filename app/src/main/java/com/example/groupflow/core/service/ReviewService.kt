package com.example.groupflow.core.service

import com.example.groupflow.core.domain.Review

interface ReviewService {
    fun submit(review: Review): Boolean
    fun listForPatient(patientId: String): List<Review>
}