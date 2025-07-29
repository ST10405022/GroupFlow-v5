package com.example.groupflow.core.service

import com.example.groupflow.core.domain.Appointment
import com.example.groupflow.core.domain.Patient
import java.time.LocalDateTime

interface AppointmentService {
    fun schedules(patient: Patient, dateTime: LocalDateTime): Appointment
    fun accepts(appointmentId: String): Appointment
    fun rejects(appointmentId: String): Appointment
    fun listForPatient(patientId: String): List<Appointment>
}