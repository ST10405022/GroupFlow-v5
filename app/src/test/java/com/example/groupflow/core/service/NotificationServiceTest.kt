package com.example.groupflow.core.service

import com.example.groupflow.core.domain.Notification
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class NotificationServiceTest {

    private lateinit var notificationService: NotificationService

    @Before
    fun setUp() {
        // Fake in-memory implementation for testing
        notificationService = object : NotificationService {
            private val notifications = mutableMapOf<String, MutableList<Notification>>()

            override suspend fun pushNotificationForUser(
                userId: String,
                notification: Notification
            ): Result<Unit> {
                val list = notifications.getOrPut(userId) { mutableListOf() }
                list.add(notification)
                return Result.success(Unit)
            }

            override suspend fun getNotificationsForUser(userId: String): Result<List<Notification>> {
                return Result.success(notifications[userId] ?: emptyList())
            }
        }
    }

    @Test
    fun `pushNotificationForUser should store notification`() = runBlocking {
        val notification = Notification(
            id = "1",
            message = "Welcome to GroupFlow!",
            timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
        )

        val result = notificationService.pushNotificationForUser("user123", notification)

        assertTrue(result.isSuccess)

        val notificationsResult = notificationService.getNotificationsForUser("user123")
        assertTrue(notificationsResult.isSuccess)
        assertEquals(1, notificationsResult.getOrNull()?.size)
        assertEquals("Welcome to GroupFlow!", notificationsResult.getOrNull()?.first()?.message)
    }

    @Test
    fun `getNotificationsForUser should return empty list when none exist`() = runBlocking {
        val notificationsResult = notificationService.getNotificationsForUser("nonexistentUser")

        assertTrue(notificationsResult.isSuccess)
        assertTrue(notificationsResult.getOrNull()?.isEmpty() == true)
    }
}
