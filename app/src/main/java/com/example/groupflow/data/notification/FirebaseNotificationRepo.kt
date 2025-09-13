package com.example.groupflow.data.notification

import com.example.groupflow.core.domain.Notification
import com.example.groupflow.core.service.NotificationService
import com.example.groupflow.models.NotificationModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class FirebaseNotificationRepo : NotificationService {

    private val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("notifications")
    private val zone = ZoneId.systemDefault()

    /**
     * Send a notification to a specific user (patient).
     */
    override suspend fun pushNotificationForUser(userId: String, notification: Notification): Result<Unit> {
        return try {
            val key = db.push().key ?: throw IllegalStateException("No key generated")
            val model = notification.toModel(key, userId)
            db.child(key).setValue(model).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch notifications for a specific user (patient) only.
     *
     */
    override suspend fun getNotificationsForUser(userId: String): Result<List<Notification>> {
        return try {
            val snapshot = db.orderByChild("recipientId").equalTo(userId).get().await()
            val list = snapshot.children.mapNotNull { it.toDomain() }
            Result.success(list.sortedByDescending { it.timestamp }) // most recent first
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Convert domain Notification -> NotificationModel for Firebase storage
     * @param key Firebase key for the notification
     * @param recipientId User ID of the recipient
     * @return NotificationModel for Firebase storage
     * @see NotificationModel
     * @see Notification
     */
    private fun Notification.toModel(key: String, recipientId: String): NotificationModel {
        val millis = this.timestamp.atZone(zone).toInstant().toEpochMilli()
        return NotificationModel(
            id = key,
            message = this.message,
            recipientId = recipientId,
            timestamp = millis,
            read = this.read,
            type = this.type,
            relatedId = this.relatedId
        )
    }

    /**
     * Convert Firebase snapshot -> domain Notification
     *
     */
    private fun DataSnapshot.toDomain(): Notification? {
        val id = child("id").getValue(String::class.java) ?: key ?: return null
        val message = child("message").getValue(String::class.java) ?: ""
        val recipientId = child("recipientId").getValue(String::class.java) ?: ""
        val millis = child("timestamp").getValue(Long::class.java) ?: 0L
        val timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), zone)
        val read = child("read").getValue(Boolean::class.java) ?: false
        return Notification(id, message, recipientId, timestamp, read)
    }
}
