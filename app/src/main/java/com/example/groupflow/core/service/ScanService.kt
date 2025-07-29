package com.example.groupflow.core.service

import com.example.groupflow.core.domain.UltrascanImage

interface ScanService {
    fun uploads(scan: UltrascanImage): Boolean
    fun retrieves(patientId: String): List<UltrascanImage>
}