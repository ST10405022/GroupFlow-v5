package com.example.groupflow.ui.appointments

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.groupflow.R
import com.example.groupflow.ToastMatcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppointmentsActivityTest {
    @Test
    fun clickAddAppointmentFAB_opensRequestAppointmentActivity() {
        ActivityScenario.launch(AppointmentsActivity::class.java)
        onView(withId(R.id.fabAddAppointment)).perform(click())
        onView(withId(R.id.topAppBarRequest)).check(matches(isDisplayed()))
    }

    @Test
    fun clickProfileMenu_opensUserProfileActivity() {
        ActivityScenario.launch(AppointmentsActivity::class.java)
        onView(withContentDescription("More options")).perform(click()) // topAppBar menu
        onView(withText("Profile")).perform(click())
        onView(withId(R.id.topAppBarProfile)).check(matches(isDisplayed()))
    }

    @Test
    fun clickLogoutMenu_showsToastAndOpensLoginActivity() {
        ActivityScenario.launch(AppointmentsActivity::class.java)
        onView(withContentDescription("More options")).perform(click())
        onView(withText("Logout")).perform(click())
        onView(withText("Logged out")).inRoot(ToastMatcher()).check(matches(isDisplayed()))
        onView(withId(R.id.loginTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun bottomNav_clicks_navigateCorrectly() {
        ActivityScenario.launch(AppointmentsActivity::class.java)
        // Home
        onView(withId(R.id.nav_home)).perform(click())
        onView(withId(R.id.topAppBar)).check(matches(isDisplayed()))

        // Back to AppointmentsActivity
        ActivityScenario.launch(AppointmentsActivity::class.java)

        // Appointments (already viewing)
        onView(withId(R.id.nav_appointments)).perform(click())
        onView(withText("Already viewing appointments"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))

        // Profile
        onView(withId(R.id.nav_profile)).perform(click())
        onView(withId(R.id.topAppBarDoctorInfo)).check(matches(isDisplayed()))

        // Notifications
        ActivityScenario.launch(AppointmentsActivity::class.java)
        onView(withId(R.id.nav_notifications)).perform(click())
        onView(withId(R.id.topAppBarNotifications)).check(matches(isDisplayed()))
    }
    // (Android Developers, n.d.)
}
