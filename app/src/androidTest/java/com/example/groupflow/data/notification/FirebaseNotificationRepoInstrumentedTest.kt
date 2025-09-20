package com.example.groupflow.data.notification

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.groupflow.core.domain.Notification
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class FirebaseNotificationRepoInstrumentedTest {
    private val repo = FirebaseNotificationRepo()

    @Test
    fun pushAndFetchNotification_shouldSucceed() =
        runBlocking {
            val notification =
                Notification(
                    id = "",
                    message = "Test message",
                    recipientId = "testUser",
                    timestamp = LocalDateTime.now(),
                    read = false,
                )

            val pushResult = repo.pushNotificationForUser("testUser", notification)
            assertTrue(pushResult.isSuccess)

            val fetchResult = repo.getNotificationsForUser("testUser")
            assertTrue(fetchResult.isSuccess)

            val notifications = fetchResult.getOrNull()
            assertNotNull(notifications)
            assertTrue(notifications!!.any { it.message == "Test message" })
        }
}
