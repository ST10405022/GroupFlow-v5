// File: app/src/main/java/com/example/groupflow/data/scan/FirebaseScanRepo.kt
package com.example.groupflow.data.scan

import com.example.groupflow.core.domain.UltrascanImage
import com.example.groupflow.core.service.ScanService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class FirebaseScanRepo : ScanService {
    private val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("scans")
    private val storageRef = FirebaseStorage.getInstance().reference.child("scans")
    private val zone = ZoneId.systemDefault()

    /**
     * Uploads a scan image to Firebase Storage and stores its metadata in Firebase Realtime Database.
     * @param patientId The ID of the patient associated with the scan.
     * @param imageStream The input stream of the scan image.
     * @param fileName The name of the scan image file.
     * @return A [Result] containing the uploaded [UltrascanImage] object or an error message.
     *
     */
    override suspend fun uploadScan(
        patientId: String,
        imageStream: InputStream,
        fileName: String?,
    ): Result<UltrascanImage> =
        try {
            // read bytes off main thread
            val bytes = withContext(Dispatchers.IO) { imageStream.readBytes() }
            val key = db.push().key ?: UUID.randomUUID().toString()
            val ext = fileName?.substringAfterLast('.', "jpg") ?: "jpg"
            val fileRef = storageRef.child("$key.$ext")
            fileRef.putBytes(bytes).await()
            val url = fileRef.downloadUrl.await().toString()

            val uploadedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), zone)
            val scan =
                UltrascanImage(
                    id = key,
                    imageUrl = url,
                    uploadedDate = uploadedDate,
                    patientId = patientId,
                    description = null,
                )

            db.child(key).setValue(toMap(scan)).await()
            Result.success(scan)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Fetches a list of [UltrascanImage] objects for a given patient ID.
     * @param patientId The ID of the patient.
     * @return A [Result] containing the list of scans or an error message.
     * @see UltrascanImage
     */
    override suspend fun fetchScansForPatient(patientId: String): Result<List<UltrascanImage>> =
        try {
            val snap =
                db
                    .orderByChild("patientId")
                    .equalTo(patientId)
                    .get()
                    .await()
            val list = snap.children.mapNotNull { snapshotToScan(it) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Converts a [UltrascanImage] object to a map.
     * @param scan The [UltrascanImage] object to convert.
     * @return A map containing the converted data.
     * @see UltrascanImage
     */
    private fun toMap(scan: UltrascanImage): Map<String, Any?> {
        val millis =
            scan.uploadedDate
                .atZone(zone)
                .toInstant()
                .toEpochMilli()
        return mapOf(
            "id" to scan.id,
            "imageUrl" to scan.imageUrl,
            "uploadedDate" to millis,
            "patientId" to scan.patientId,
            "description" to scan.description,
        )
    }

    /**
     * Converts a Firebase [DataSnapshot] to a [UltrascanImage] object.
     * @param snapshot The Firebase [DataSnapshot] to convert.
     * @return The converted [UltrascanImage] object, or null if the conversion fails.
     * @throws Exception if the conversion fails.
     * @see UltrascanImage
     */
    private fun snapshotToScan(snapshot: DataSnapshot): UltrascanImage? {
        val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: return null
        val imageUrl = snapshot.child("imageUrl").getValue(String::class.java) ?: ""
        val millis = snapshot.child("uploadedDate").getValue(Long::class.java) ?: 0L
        val uploadedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), zone)
        val patientId = snapshot.child("patientId").getValue(String::class.java) ?: ""
        val description = snapshot.child("description").getValue(String::class.java)
        return UltrascanImage(id, imageUrl, uploadedDate, patientId, description)
    }
}
