package com.example.groupflow.core.service

import com.example.groupflow.core.domain.UltrascanImage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.time.LocalDateTime

class ScanServiceTest {
    private lateinit var scanService: ScanService
    // Fake in-memory implementation for testing
    class FakeScanService : ScanService {
        private val scans = mutableListOf<UltrascanImage>()

        override suspend fun uploadScan(
            patientId: String,
            imageStream: java.io.InputStream,
            fileName: String?
        ): Result<UltrascanImage> {
            val bytes = imageStream.readBytes()
            val scan = UltrascanImage(
                id = (scans.size + 1).toString(),
                patientId = patientId,
                uploadedDate = LocalDateTime.now(),
                imageUrl = fileName ?: "scan_${scans.size + 1}.jpg"
            )
            scans.add(scan)
            return Result.success(scan)
        }

        override suspend fun fetchScansForPatient(patientId: String): Result<List<UltrascanImage>> {
            return Result.success(scans.filter { it.patientId == patientId })
        }
    }

    @Before
    fun setup() {
        scanService = FakeScanService()
    }

    @Test
    fun testUploadScanAndFetch() = runBlocking {
        val patientId = "patient123"
        val fileName = "test_scan.jpg"
        val inputStream = ByteArrayInputStream(byteArrayOf(1, 2, 3))

        // Upload scan
        val result = scanService.uploadScan(patientId, inputStream, fileName)
        assertTrue(result.isSuccess)

        val scan = result.getOrNull()
        assertNotNull(scan)
        assertEquals(patientId, scan?.patientId)
        assertEquals(fileName, scan?.imageUrl)

        // Fetch scans for patient
        val fetchResult = scanService.fetchScansForPatient(patientId)
        assertTrue(fetchResult.isSuccess)
        val scans = fetchResult.getOrNull()
        assertNotNull(scans)
        assertEquals(1, scans?.size)
        assertEquals(fileName, scans?.get(0)?.imageUrl)
    }
}
