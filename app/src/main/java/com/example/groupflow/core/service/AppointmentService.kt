package com.example.groupflow.core.service

import com.example.groupflow.core.domain.Appointment
import com.example.groupflow.core.domain.Patient
import java.time.LocalDateTime

interface AppointmentService {
    suspend fun schedules(
        patient: Patient,
        dateTime: LocalDateTime,
    ): Appointment

    suspend fun accepts(appointmentId: String): Appointment

    suspend fun rejects(appointmentId: String): Appointment

    suspend fun listForPatient(patientId: String): List<Appointment>

    suspend fun createAppointment(appointment: Appointment): Result<Unit>

    suspend fun getAppointmentsForUser(userId: String): Result<List<Appointment>>

    suspend fun updateAppointment(appointment: Appointment): Result<Unit>
}
