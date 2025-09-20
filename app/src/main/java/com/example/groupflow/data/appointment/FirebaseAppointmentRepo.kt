package com.example.groupflow.data.appointment

import com.example.groupflow.core.domain.Appointment
import com.example.groupflow.core.domain.Patient
import com.example.groupflow.core.service.AppointmentService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class FirebaseAppointmentRepo : AppointmentService {
    private val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("appointments")
    private val zone = ZoneId.systemDefault()

    // Employee schedules an appointment for patient at dateTime -> returns created Appointment
    override suspend fun schedules(
        patient: Patient,
        dateTime: LocalDateTime,
    ): Appointment {
        val key = db.push().key ?: throw IllegalStateException("Unable to generate key")
        val appointment =
            Appointment(
                id = key,
                requestedDate = dateTime,
                status = Appointment.Status.APPROVED,
                reason = "",
                patientId = patient.id,
                employeeId = "",
            )
        db.child(key).setValue(toMap(appointment)).await()
        return appointment
    }

    // Accept (approve) an appointment by id -> returns updated Appointment
    override suspend fun accepts(appointmentId: String): Appointment {
        val snap = db.child(appointmentId).get().await()
        val appt = snapshotToAppointment(snap) ?: throw IllegalStateException("Appointment not found")
        val updated = appt.copy(status = Appointment.Status.APPROVED)
        db.child(appointmentId).setValue(toMap(updated)).await()
        return updated
    }

    // Reject (decline) an appointment by id -> returns updated Appointment
    override suspend fun rejects(appointmentId: String): Appointment {
        val snap = db.child(appointmentId).get().await()
        val appt = snapshotToAppointment(snap) ?: throw IllegalStateException("Appointment not found")
        val updated = appt.copy(status = Appointment.Status.DECLINED)
        db.child(appointmentId).setValue(toMap(updated)).await()
        return updated
    }

    // List appointments for a patient (synchronous return)
    override suspend fun listForPatient(patientId: String): List<Appointment> {
        val snap =
            db
                .orderByChild("patientId")
                .equalTo(patientId)
                .get()
                .await()
        return snap.children.mapNotNull { snapshotToAppointment(it) }
    }

    // Create (general) appointment — return Result<Unit>
    override suspend fun createAppointment(appointment: Appointment): Result<Unit> =
        try {
            val key = appointment.id.ifBlank { db.push().key!! }
            db.child(key).setValue(toMap(appointment.copy(id = key))).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getAppointmentsForUser(userId: String): Result<List<Appointment>> =
        try {
            val snap =
                db
                    .orderByChild("patientId")
                    .equalTo(userId)
                    .get()
                    .await()
            val list = snap.children.mapNotNull { snapshotToAppointment(it) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun updateAppointment(appointment: Appointment): Result<Unit> =
        try {
            if (appointment.id.isBlank()) throw IllegalArgumentException("Appointment id required")
            db.child(appointment.id).setValue(toMap(appointment)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    // Helper: convert Appointment -> Map (storable in Firebase)
    private fun toMap(a: Appointment): Map<String, Any?> {
        val millis =
            a.requestedDate
                .atZone(zone)
                .toInstant()
                .toEpochMilli()
        return mapOf(
            "id" to a.id,
            "requestedDate" to millis,
            "status" to a.status.name,
            "reason" to a.reason,
            "patientId" to a.patientId,
            "employeeId" to a.employeeId,
        )
    }

    // Helper: convert DataSnapshot -> Appointment
    private fun snapshotToAppointment(snapshot: DataSnapshot): Appointment? {
        val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: return null
        val requestedMillis = snapshot.child("requestedDate").getValue(Long::class.java) ?: 0L
        val requestedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(requestedMillis), zone)
        val statusStr = snapshot.child("status").getValue(String::class.java) ?: Appointment.Status.PENDING.name
        val status =
            try {
                Appointment.Status.valueOf(statusStr)
            } catch (_: Exception) {
                Appointment.Status.PENDING
            }
        val reason = snapshot.child("reason").getValue(String::class.java) ?: ""
        val patientId = snapshot.child("patientId").getValue(String::class.java) ?: ""
        val employeeId = snapshot.child("employeeId").getValue(String::class.java) ?: ""
        return Appointment(id, requestedDate, status, reason, patientId, employeeId)
    }
}
