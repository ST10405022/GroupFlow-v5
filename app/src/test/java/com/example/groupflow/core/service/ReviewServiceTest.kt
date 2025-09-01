package com.example.groupflow.core.service

import com.example.groupflow.core.domain.Review
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class ReviewServiceTest {

    private lateinit var reviewService: ReviewService

    @Before
    fun setup() {
        // Here we would use a fake or mock implementation for unit testing
        reviewService = object : ReviewService {
            private val reviews = mutableListOf<Review>()

            override suspend fun addReview(review: Review): Result<Unit> {
                reviews.add(review)
                return Result.success(Unit)
            }

            override suspend fun fetchReviewsForClinic(clinicId: String): Result<List<Review>> {
                return Result.success(reviews.filter { it.clinicId == clinicId })
            }
        }
    }

    @Test
    fun testAddReview() = runBlocking {
        val review = Review(
            id = "1",
            patientId = "patient1",
            clinicId = "clinic1",
            rating = 5,
            comment = "Excellent service!",
            createdDate = LocalDateTime.now()
        )

        val result = reviewService.addReview(review)
        assertTrue(result.isSuccess)
    }

    @Test
    fun testFetchReviewsForClinic() = runBlocking {
        val review1 = Review("1", "patient1", "clinic1", 5, "Great!", LocalDateTime.now())
        val review2 = Review("2", "patient2", "clinic1", 4, "Good!", LocalDateTime.now())
        val review3 = Review("3", "patient3", "clinic2", 3, "Okay.", LocalDateTime.now())

        reviewService.addReview(review1)
        reviewService.addReview(review2)
        reviewService.addReview(review3)

        val result = reviewService.fetchReviewsForClinic("clinic1")
        assertTrue(result.isSuccess)

        val reviewsForClinic = result.getOrNull()
        assertNotNull(reviewsForClinic)
        assertEquals(2, reviewsForClinic?.size)
        assertTrue(reviewsForClinic!!.all { it.clinicId == "clinic1" })
    }
}
