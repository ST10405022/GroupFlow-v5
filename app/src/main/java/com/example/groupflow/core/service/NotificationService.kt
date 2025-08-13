package com.example.groupflow.core.service

import com.example.groupflow.core.domain.Notification

interface NotificationService {
    suspend fun pushNotificationForUser(userId: String, notification: Notification): Result<Unit>
    suspend fun getNotificationsForUser(userId: String): Result<List<Notification>>
}
