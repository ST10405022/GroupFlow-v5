package com.example.groupflow.data.review

import com.example.groupflow.core.domain.Review
import com.example.groupflow.core.service.ReviewService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class FirebaseReviewRepo : ReviewService {
    private val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("reviews")
    private val zone = ZoneId.systemDefault()

    /**
     * Adds a review to the Firebase Realtime Database.
     * @param review The review to add.
     * @return A [Result] containing the result of the operation.
     * @throws Exception if the operation fails.
     * @see Review
     */
    override suspend fun addReview(review: Review): Result<Unit> =
        try {
            val key = review.id.ifBlank { db.push().key!! }
            db.child(key).setValue(toMap(review.copy(id = key))).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Fetches a list of reviews for a given clinic ID.
     * @param clinicId The ID of the clinic.
     * @return A [Result] containing the list of reviews or an error message.
     * @throws Exception if the operation fails.
     * @see Review
     */
    override suspend fun fetchReviewsForClinic(clinicId: String): Result<List<Review>> =
        try {
            val snapshot =
                db
                    .orderByChild("clinicId")
                    .equalTo(clinicId)
                    .get()
                    .await()
            val list = snapshot.children.mapNotNull { snapshotToReview(it) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Fetches all reviews from the database.
     * @return A [Result] containing the list of all reviews or an error.
     * @throws Exception if the operation fails.
     * @see Review
     */
    suspend fun fetchAllReviews(): Result<List<Review>> =
        try {
            // Simply get all data at the "reviews" reference without any filtering
            val snapshot = db.get().await()
            val list = snapshot.children.mapNotNull { snapshotToReview(it) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Converts a [Review] object to a map.
     * @param r The [Review] object to convert.
     * @return A map containing the converted data.
     * @see Review
     */
    private fun toMap(r: Review): Map<String, Any?> {
        val millis =
            r.createdDate
                .atZone(zone)
                .toInstant()
                .toEpochMilli()
        return mapOf(
            "id" to r.id,
            "patientId" to r.patientId,
            "clinicId" to r.clinicId,
            "rating" to r.rating,
            "comment" to r.comment,
            "createdDate" to millis,
        )
    }

    private fun snapshotToReview(snapshot: DataSnapshot): Review? {
        val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: return null
        val patientId = snapshot.child("patientId").getValue(String::class.java) ?: ""
        val clinicId = snapshot.child("clinicId").getValue(String::class.java) ?: ""
        val rating = snapshot.child("rating").getValue(Int::class.java) ?: 0
        val comment = snapshot.child("comment").getValue(String::class.java) ?: ""
        val millis = snapshot.child("createdDate").getValue(Long::class.java) ?: 0L
        val createdDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), zone)
        return Review(id, patientId, clinicId, rating, comment, createdDate)
    }
}
