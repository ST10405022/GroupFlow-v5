package com.example.groupflow.core.service

import com.example.groupflow.core.domain.Notification

interface NotificationService {
    fun triggers(notification: Notification): Boolean
    fun listForRecipient(recipientId: String): List<Notification>
}