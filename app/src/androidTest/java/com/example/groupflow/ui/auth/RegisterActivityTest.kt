package com.example.groupflow.ui.auth

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.groupflow.R
import com.example.groupflow.ToastMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RegisterActivity::class.java)

    @Test
    fun register_withEmptyFields_showsErrorMessage() {
        onView(withId(R.id.buttonRegister)).perform(click())
        onView(withText("Please fill in all fields with valid input"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun register_withInvalidEmail_showsErrorMessage() {
        onView(withId(R.id.editTextName)).perform(typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("invalidEmail"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.spinnerRole)).perform(click())
        onView(withText("PATIENT")).perform(click())
//(Android Developers, n.d.)
        onView(withId(R.id.buttonRegister)).perform(click())

        onView(withText("Please fill in all fields with valid input"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    // You could mock AppDatabase/authService to simulate success/failure
    // and assert navigation (MainActivity/EmployeeHubActivity).
}
