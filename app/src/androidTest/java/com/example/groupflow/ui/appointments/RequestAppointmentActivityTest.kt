package com.example.groupflow.ui.appointments

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.groupflow.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestAppointmentActivityTest {

    @Test
    fun testUiElementsAreDisplayed() {
        ActivityScenario.launch(RequestAppointmentActivity::class.java)

        // Check top app bar
        onView(withId(R.id.topAppBarRequest))
            .check(matches(isDisplayed()))

        // Check bottom navigation
        onView(withId(R.id.bottomNav))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBottomNavigationAppointmentsNavigation() {
        ActivityScenario.launch(RequestAppointmentActivity::class.java)

        // Navigate to Appointments tab
        onView(withId(R.id.nav_appointments)).perform(click())
    }

    @Test
    fun testBottomNavigationProfileNavigation() {
        ActivityScenario.launch(RequestAppointmentActivity::class.java)

        // Navigate to Profile tab
        onView(withId(R.id.nav_profile)).perform(click())
    }

    @Test
    fun testBottomNavigationNotificationsNavigation() {
        ActivityScenario.launch(RequestAppointmentActivity::class.java)

        // Navigate to Notifications tab
        onView(withId(R.id.nav_notifications)).perform(click())
    }
}
