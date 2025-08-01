package com.example.groupflow.data.notification

import com.example.groupflow.core.domain.Notification
import com.example.groupflow.core.service.NotificationService

class InMemoryNotificationRepo : NotificationService {
    private val notifications = mutableListOf<Notification>()
    override fun triggers(notification: Notification) = notifications.add(notification)
    override fun listForRecipient(recipientId: String) =
        notifications.filter { it.recipientId == recipientId }
}
