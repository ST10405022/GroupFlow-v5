package com.example.groupflow.ui.auth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.groupflow.core.domain.Employee
import com.example.groupflow.core.domain.Patient
import com.example.groupflow.core.domain.Role
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionCreationTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        SessionCreation.logout(context) // clear before each test
    }

    @Test
    fun saveAndRetrievePatient() {
        val patient = Patient("1", "John Doe", "john@example.com", Role.PATIENT)
        SessionCreation.saveUser(context, patient)

        val retrieved = SessionCreation.getUser(context)

        Assert.assertNotNull(retrieved)
        Assert.assertEquals(patient.id, retrieved?.id)
        Assert.assertEquals(patient.name, retrieved?.name)
        Assert.assertEquals(patient.email, retrieved?.email)
        Assert.assertEquals(patient.role, retrieved?.role)
    }

    @Test
    fun saveAndRetrieveEmployee() {
        val employee = Employee("2", "Jane Smith", "jane@example.com", Role.EMPLOYEE)
        SessionCreation.saveUser(context, employee)

        val retrieved = SessionCreation.getUser(context)

        Assert.assertNotNull(retrieved)
        Assert.assertEquals(employee.id, retrieved?.id)
        Assert.assertEquals(employee.role, retrieved?.role)
    }

    @Test
    fun loggedInReturnsTrueWhenUserSaved() {
        val patient = Patient("3", "Alice", "alice@example.com", Role.PATIENT)
        SessionCreation.saveUser(context, patient)

        Assert.assertTrue(SessionCreation.loggedIn(context))
    }

    @Test
    fun loggedInReturnsFalseWhenNoUser() {
        Assert.assertFalse(SessionCreation.loggedIn(context))
    }

    @Test
    fun logoutClearsSession() {
        val employee = Employee("4", "Bob", "bob@example.com", Role.EMPLOYEE)
        SessionCreation.saveUser(context, employee)

        SessionCreation.logout(context)

        Assert.assertNull(SessionCreation.getUser(context))
        Assert.assertFalse(SessionCreation.loggedIn(context))
    }
}