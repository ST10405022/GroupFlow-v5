package com.example.groupflow.data.review

import com.example.groupflow.core.domain.Review
import com.example.groupflow.core.service.ReviewService

class InMemoryReviewRepo : ReviewService {
    private val reviews = mutableListOf<Review>()
    override fun submit(review: Review) = reviews.add(review)
    override fun listForPatient(patientId: String) =
        reviews.filter { it.patientId == patientId }
}
