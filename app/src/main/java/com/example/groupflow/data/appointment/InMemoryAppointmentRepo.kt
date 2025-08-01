package com.example.groupflow.data.appointment

import com.example.groupflow.core.domain.Appointment
import com.example.groupflow.core.domain.Patient
import com.example.groupflow.core.service.AppointmentService
import java.time.LocalDateTime
import java.util.*

class InMemoryAppointmentRepo : AppointmentService {
    private val appointments = mutableListOf<Appointment>()

    override fun schedules(patient: Patient, dateTime: LocalDateTime) =
        Appointment(UUID.randomUUID().toString(), dateTime, patientId = patient.id)
            .also { appointments += it }

    override fun accepts(appointmentId: String) =
        updateStatus(appointmentId, Appointment.Status.APPROVED)

    override fun rejects(appointmentId: String) =
        updateStatus(appointmentId, Appointment.Status.DECLINED)

    override fun listForPatient(patientId: String) =
        appointments.filter { it.patientId == patientId }

    private fun updateStatus(id: String, status: Appointment.Status): Appointment {
        val appt = appointments.first { it.id == id }
        val updated = appt.copy(status = status)
        appointments.replaceAll { if (it.id == id) updated else it }
        return updated
    }
}
