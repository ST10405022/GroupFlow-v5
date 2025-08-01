package com.example.groupflow.data.scan

import com.example.groupflow.core.domain.UltrascanImage
import com.example.groupflow.core.service.ScanService

class InMemoryScanRepo : ScanService {
    private val scans = mutableListOf<UltrascanImage>()
    override fun uploads(scan: UltrascanImage) = scans.add(scan)
    override fun retrieves(patientId: String) =
        scans.filter { it.patientId == patientId }
}
