package com.example.groupflow.data.notification

import com.example.groupflow.core.domain.Notification
import com.example.groupflow.core.service.NotificationService
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
     * Sends a push notification to a specific user.
     * @param userId The ID of the user to send the notification to.
     * @param notification The notification to send.
     * @return A [Result] containing the result of the operation.
     * @throws Exception if the operation fails.
     * @see Notification
     */
    override suspend fun pushNotificationForUser(userId: String, notification: Notification): Result<Unit> {
        return try {
            val key = db.push().key ?: throw IllegalStateException("No key")
            val map = toMap(notification.copy(id = key, recipientId = userId))
            db.child(key).setValue(map).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marks a notification as read.
     * @param userId The ID of the user who received the notification.
     * @return A [Result] containing the result of the operation.
     * @throws Exception if the operation fails.
     * @see Notification
     */
    override suspend fun getNotificationsForUser(userId: String): Result<List<Notification>> {
        return try {
            val snap = db.orderByChild("recipientId").equalTo(userId).get().await()
            val list = snap.children.mapNotNull { snapshotToNotification(it) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Converts a [Notification] object to a map.
     * @param n The [Notification] object to convert.
     * @return A map containing the converted data.
     * @see Notification
     */
    private fun toMap(n: Notification): Map<String, Any?> {
        val millis = n.timestamp.atZone(zone).toInstant().toEpochMilli()
        return mapOf(
            "id" to n.id,
            "message" to n.message,
            "recipientId" to n.recipientId,
            "timestamp" to millis,
            "read" to n.read
        )
    }

    /**
     * Converts a Firebase [DataSnapshot] to a [Notification] object.
     * @param snapshot The Firebase [DataSnapshot] to convert.
     * @return The converted [Notification] object, or null if the conversion fails.
     * @throws Exception if the conversion fails.
     * @see Notification
     */
    private fun snapshotToNotification(snapshot: DataSnapshot): Notification? {
        val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: return null
        val message = snapshot.child("message").getValue(String::class.java) ?: ""
        val recipientId = snapshot.child("recipientId").getValue(String::class.java) ?: ""
        val millis = snapshot.child("timestamp").getValue(Long::class.java) ?: 0L
        val timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), zone)
        val read = snapshot.child("read").getValue(Boolean::class.java) ?: false
        return Notification(id, message, recipientId, timestamp, read)
    }
}
