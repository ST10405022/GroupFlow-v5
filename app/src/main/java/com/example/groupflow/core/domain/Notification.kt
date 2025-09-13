package com.example.groupflow.core.domain

import java.time.LocalDateTime

data class Notification(
    val id: String = "",
    val message: String = "",
    val recipientId: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val read: Boolean = false,
    val type: String? = null,
    val relatedId: String? = null
)