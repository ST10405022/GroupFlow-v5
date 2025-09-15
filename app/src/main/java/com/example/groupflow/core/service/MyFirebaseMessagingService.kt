package com.example.groupflow.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.groupflow.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.groupflow.R
import com.example.groupflow.ui.auth.SessionCreation
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.example.groupflow.ui.notifications.NotificationsActivity
import com.google.firebase.database.FirebaseDatabase

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Data payload: ${remoteMessage.data}")

        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: "New Message"
        val message = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: ""
        val clickAction = remoteMessage.data["clickActionTarget"]

        // Send notification to user
        sendNotification(title, message, clickAction)
    }

    private fun sendNotification(title: String, message: String, clickAction: String?) {
        val channelId = "groupflow_notifications"
        val notificationId = System.currentTimeMillis().toInt()

        // Get logged-in user
        val currentUser = SessionCreation.getUser(this)

        // Default intent
        var intent = Intent(this, MainActivity::class.java)

        if (currentUser != null) {
            if (currentUser.role.name == "PATIENT") {
                // Patients always go to NotificationsActivity
                intent = Intent(this, NotificationsActivity::class.java).apply {
                    putExtra("clickActionTarget", clickAction)
                }
            } else if (currentUser.role.name == "EMPLOYEE") {
                // Employees go straight to their hub
                intent = Intent(this, EmployeeHubActivity::class.java)
            }
        }

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "GroupFlow Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        val currentUser = SessionCreation.getUser(this)
        currentUser?.let {
            FirebaseDatabase.getInstance().getReference("users/${it.id}/fcmToken")
                .setValue(token)
        }
    }
}
