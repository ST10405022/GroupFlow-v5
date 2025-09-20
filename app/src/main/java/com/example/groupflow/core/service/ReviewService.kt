package com.example.groupflow.core.service

import com.example.groupflow.core.domain.Review

interface ReviewService {
    suspend fun addReview(review: Review): Result<Unit>

    suspend fun fetchReviewsForClinic(clinicId: String): Result<List<Review>>
}
