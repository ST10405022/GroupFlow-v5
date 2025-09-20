package com.example.groupflow.core.service

import com.example.groupflow.core.domain.UltrascanImage
import java.io.InputStream

interface ScanService {
    suspend fun uploadScan(
        patientId: String,
        imageStream: InputStream,
        fileName: String?,
    ): Result<UltrascanImage>

    suspend fun fetchScansForPatient(patientId: String): Result<List<UltrascanImage>>
}
