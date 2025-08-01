package com.example.groupflow.data

import com.example.groupflow.core.service.AppointmentService
import com.example.groupflow.core.service.AuthenticationService
import com.example.groupflow.core.service.ScanService
import com.example.groupflow.core.service.ReviewService
import com.example.groupflow.data.appointment.InMemoryAppointmentRepo
import com.example.groupflow.data.auth.InMemoryAuthAdapter
import com.example.groupflow.data.review.InMemoryReviewRepo
import com.example.groupflow.data.scan.InMemoryScanRepo
import com.example.groupflow.data.notification.InMemoryNotificationRepo

object AppDatabase {

    // Authentication service instance (currently in-memory)
    val authService: AuthenticationService by lazy {
        InMemoryAuthAdapter()
        // TODO: Replace with FirebaseAuthAdapter() when Firebase is integrated
    }

    // Appointment repository instance
    val appointmentService: AppointmentService by lazy {
        InMemoryAppointmentRepo()
        // TODO: Replace with FirebaseAppointmentRepo() or Room DB as needed
    }

    // Imaging (Ultrascan) repository instance
    val imagingService: ScanService by lazy {
        InMemoryScanRepo()
        // TODO: Replace with FirebaseStorageScanRepo() for ultrasound image upload
    }

    // Review repository instance
    val reviewService: ReviewService by lazy {
        InMemoryReviewRepo()
        // TODO: Replace with FirebaseReviewRepo() for submitting and fetching reviews
    }

    // Notifications repository (not a core service interface yet)
    val notificationRepo = InMemoryNotificationRepo()
    // TODO: Add NotificationService interface if needed and replace with FirebaseNotificationRepo
}
