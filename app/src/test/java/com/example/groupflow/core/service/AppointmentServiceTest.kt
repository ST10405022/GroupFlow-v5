package com.example.groupflow.core.service

import com.example.groupflow.core.domain.Appointment
import com.example.groupflow.core.domain.Patient
import com.example.groupflow.core.domain.Role
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class AppointmentServiceTest {

    private lateinit var service: AppointmentService
    private lateinit var patient: Patient

    @Before
    fun setUp() {
        service = object : AppointmentService {
            private val appointments = mutableListOf<Appointment>()

            override suspend fun schedules(patient: Patient, dateTime: LocalDateTime): Appointment {
                val appointment = Appointment(
                    id = "a1",
                    requestedDate = dateTime,
                    status = Appointment.Status.PENDING,
                    reason = "Routine check-up",
                    patientId = patient.id,
                    employeeId = "e1"
                )
                appointments.add(appointment)
                return appointment
            }

            override suspend fun accepts(appointmentId: String): Appointment {
                val appointment = appointments.find { it.id == appointmentId }
                    ?: throw IllegalArgumentException("Not found")
                val updated = appointment.copy(status = Appointment.Status.APPROVED)
                appointments[appointments.indexOf(appointment)] = updated
                return updated
            }

            override suspend fun rejects(appointmentId: String): Appointment {
                val appointment = appointments.find { it.id == appointmentId }
                    ?: throw IllegalArgumentException("Not found")
                val updated = appointment.copy(status = Appointment.Status.DECLINED)
                appointments[appointments.indexOf(appointment)] = updated
                return updated
            }

            override suspend fun listForPatient(patientId: String): List<Appointment> {
                return appointments.filter { it.patientId == patientId }
            }

            override suspend fun createAppointment(appointment: Appointment): Result<Unit> {
                appointments.add(appointment)
                return Result.success(Unit)
            }

            override suspend fun getAppointmentsForUser(userId: String): Result<List<Appointment>> {
                return Result.success(appointments.filter { it.patientId == userId || it.employeeId == userId })
            }

            override suspend fun updateAppointment(appointment: Appointment): Result<Unit> {
                val existing = appointments.find { it.id == appointment.id }
                    ?: return Result.failure(IllegalArgumentException("Not found"))
                appointments[appointments.indexOf(existing)] = appointment
                return Result.success(Unit)
            }
        }

        // Create a test patient
        patient = Patient(
            id = "p1",
            name = "John Doe",
            email = "john@example.com",
            role = Role.PATIENT
        )
    }

    @Test
    fun testScheduleAppointment() = runBlocking {
        val dateTime = LocalDateTime.now().plusDays(1) // must be future date
        val appointment = service.schedules(patient, dateTime)

        assertEquals("p1", appointment.patientId)
        assertEquals("e1", appointment.employeeId)
        assertEquals(Appointment.Status.PENDING, appointment.status)
        assertTrue(appointment.requestedDate.isAfter(LocalDateTime.now()))
    }

    @Test
    fun testAcceptAppointment() = runBlocking {
        val dateTime = LocalDateTime.now().plusDays(1)
        val appointment = service.schedules(patient, dateTime)

        val accepted = service.accepts(appointment.id)
        assertEquals(Appointment.Status.APPROVED, accepted.status)
    }

    @Test
    fun testRejectAppointment() = runBlocking {
        val dateTime = LocalDateTime.now().plusDays(1)
        val appointment = service.schedules(patient, dateTime)

        val rejected = service.rejects(appointment.id)
        assertEquals(Appointment.Status.DECLINED, rejected.status)
    }

    @Test
    fun testListForPatient() = runBlocking {
        val dateTime = LocalDateTime.now().plusDays(1)
        service.schedules(patient, dateTime)

        val list = service.listForPatient("p1")
        assertEquals(1, list.size)
        assertEquals("p1", list.first().patientId)
    }
}
