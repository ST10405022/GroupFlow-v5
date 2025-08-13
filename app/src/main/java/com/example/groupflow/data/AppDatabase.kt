package com.example.groupflow.data

import com.example.groupflow.core.service.*
import com.example.groupflow.data.auth.FirebaseAuthAdapter
import com.example.groupflow.data.appointment.FirebaseAppointmentRepo
import com.example.groupflow.data.notification.FirebaseNotificationRepo
import com.example.groupflow.data.review.FirebaseReviewRepo
import com.example.groupflow.data.scan.FirebaseScanRepo

object AppDatabase {
    val authService: AuthenticationService by lazy { FirebaseAuthAdapter() }
    val appointmentService: AppointmentService by lazy { FirebaseAppointmentRepo() }
    val imagingService: ScanService by lazy { FirebaseScanRepo() }
    val reviewService: ReviewService by lazy { FirebaseReviewRepo() }
    val notificationService: NotificationService by lazy { FirebaseNotificationRepo() }
}
