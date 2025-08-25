package com.example.groupflow

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.groupflow.ui.auth.LoginActivity
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Test
    fun testLoginUiElementsAreDisplayed() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Check email and password fields + button are visible
        onView(withId(R.id.editTextEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()))
    }

    @Test
    fun testUserCanTypeCredentials() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Type email and password
        onView(withId(R.id.editTextEmail))
            .perform(typeText("test@example.com"), closeSoftKeyboard())

        onView(withId(R.id.editTextPassword))
            .perform(typeText("123456"), closeSoftKeyboard())

        // Assert text was typed
        onView(withId(R.id.editTextEmail))
            .check(matches(withText("test@example.com")))
        onView(withId(R.id.editTextPassword))
            .check(matches(withText("123456")))
    }

    @Test
    fun testClickLoginButton_showsToastOnEmptyFields() {
        val scenario = ActivityScenario.launch(LoginActivity::class.java)

        // Click login with empty fields
        onView(withId(R.id.buttonLogin)).perform(click())

        // Espresso cannot directly read Toasts → we check indirectly via decorView
        // You’ll need to set up ToastMatcher
        onView(withText(containsString("Email and Password required")))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))

        scenario.close()
    }
    //(Android Developers, n.d.)
}
