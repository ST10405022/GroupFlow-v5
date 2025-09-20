@file:Suppress("UNCHECKED_CAST")

package com.example.groupflow.data.review

import com.example.groupflow.core.domain.Review
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseReviewRepoTest {
    private lateinit var repo: FirebaseReviewRepo
    private lateinit var mockDb: DatabaseReference

    @Before
    fun setup() {
        // Mock DatabaseReference
        mockDb = mock()
        repo = FirebaseReviewRepo()
    }

    @Test
    fun `toMap should correctly convert Review to map`() {
        val review =
            Review(
                id = "123",
                patientId = "p1",
                clinicId = "c1",
                rating = 4,
                comment = "Great service",
                createdDate = LocalDateTime.now(),
            )

        val map = repoTestHelperToMap(review)

        assertEquals("123", map["id"])
        assertEquals("p1", map["patientId"])
        assertEquals("c1", map["clinicId"])
        assertEquals(4, map["rating"])
        assertEquals("Great service", map["comment"])
        assertTrue(map["createdDate"] is Long)
    }

    @Test
    fun `snapshotToReview should return null if id is missing`() {
        val snapshot =
            mock<DataSnapshot> {
                on { child("id") } doReturn mock { on { getValue(String::class.java) } doReturn null }
                on { key } doReturn null
            }

        val result = repoTestHelperSnapshotToReview(snapshot)
        assertNull(result)
    }

    // Helpers: use reflection to call private methods
    private fun repoTestHelperToMap(review: Review): Map<String, Any?> {
        val method = FirebaseReviewRepo::class.java.getDeclaredMethod("toMap", Review::class.java)
        method.isAccessible = true
        return method.invoke(repo, review) as Map<String, Any?>
    }

    private fun repoTestHelperSnapshotToReview(snapshot: DataSnapshot): Review? {
        val method = FirebaseReviewRepo::class.java.getDeclaredMethod("snapshotToReview", DataSnapshot::class.java)
        method.isAccessible = true
        return method.invoke(repo, snapshot) as Review?
    }
}
