package com.example.groupflow.core.domain

import java.time.LocalDateTime
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
data class Notification(
    val id: String,
    val message: String,
    val recipientId: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val read: Boolean = false
)