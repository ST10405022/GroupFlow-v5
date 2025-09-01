package com.example.groupflow.models

data class UltrascanModel(
    val id: String = "",
    val fileUrl: String = "",
    val uploadedAt: Long = 0,
    val patientId: String = "",
    val description: String? = null,
    val fileName: String = ""
)
