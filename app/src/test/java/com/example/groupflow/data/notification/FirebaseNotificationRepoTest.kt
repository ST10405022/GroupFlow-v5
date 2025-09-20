package com.example.groupflow.data.notification

import com.example.groupflow.core.domain.Notification
import com.google.firebase.database.DataSnapshot
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import java.time.LocalDateTime

class FirebaseNotificationRepoTest {
    private val repo = FirebaseNotificationRepo()

    @Test
    fun `toMap should convert Notification to map correctly`() {
        val notification =
            Notification(
                id = "123",
                message = "Hello",
                recipientId = "user1",
                timestamp = LocalDateTime.of(2025, 1, 1, 12, 0),
                read = false,
            )

        val map = repo.invokePrivateToMap(notification)

        assertEquals("123", map["id"])
        assertEquals("Hello", map["message"])
        assertEquals("user1", map["recipientId"])
        assertEquals(false, map["read"])
        assertTrue(map["timestamp"] is Long)
    }

    @Test
    fun `snapshotToNotification should convert snapshot to Notification`() {
        val snapshot = mock(DataSnapshot::class.java)
        val child = mock(DataSnapshot::class.java)

        // Stub children
        `when`(snapshot.child("id")).thenReturn(child)
        `when`(snapshot.child("message")).thenReturn(child)
        `when`(snapshot.child("recipientId")).thenReturn(child)
        `when`(snapshot.child("timestamp")).thenReturn(child)
        `when`(snapshot.child("read")).thenReturn(child)

        `when`(child.getValue(String::class.java)).thenReturn("123", "Hello", "user1")
        `when`(child.getValue(Long::class.java)).thenReturn(1735665600000L) // 2025-01-01
        `when`(child.getValue(Boolean::class.java)).thenReturn(false)

        val notification = repo.invokePrivateSnapshotToNotification(snapshot)

        assertNotNull(notification)
        assertEquals("123", notification?.id)
        assertEquals("Hello", notification?.message)
        assertEquals("user1", notification?.recipientId)
    }

    private fun FirebaseNotificationRepo.invokePrivateToMap(n: Notification) =
        this.javaClass
            .getDeclaredMethod("toMap", Notification::class.java)
            .apply { isAccessible = true }
            .invoke(this, n) as Map<*, *>

    private fun FirebaseNotificationRepo.invokePrivateSnapshotToNotification(snapshot: DataSnapshot) =
        this.javaClass
            .getDeclaredMethod("snapshotToNotification", DataSnapshot::class.java)
            .apply { isAccessible = true }
            .invoke(this, snapshot) as Notification?
}
