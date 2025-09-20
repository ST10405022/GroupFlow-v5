package com.example.groupflow.ui.auth

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.groupflow.R
import com.example.groupflow.ToastMatcher
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @Test
    fun testLoginUiElementsAreDisplayed() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Check email and password fields + button are visible
        Espresso
            .onView(ViewMatchers.withId(R.id.editTextEmail))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso
            .onView(ViewMatchers.withId(R.id.editTextPassword))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso
            .onView(ViewMatchers.withId(R.id.buttonLogin))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testUserCanTypeCredentials() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Type email and password
        Espresso
            .onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.typeText("test@example.com"), ViewActions.closeSoftKeyboard())

        Espresso
            .onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.typeText("123456"), ViewActions.closeSoftKeyboard())

        // Assert text was typed
        Espresso
            .onView(ViewMatchers.withId(R.id.editTextEmail))
            .check(ViewAssertions.matches(ViewMatchers.withText("test@example.com")))
        Espresso
            .onView(ViewMatchers.withId(R.id.editTextPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText("123456")))
    }

    @Test
    fun testClickLoginButton_showsToastOnEmptyFields() {
        val scenario = ActivityScenario.launch(LoginActivity::class.java)

        // Click login with empty fields
        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin)).perform(ViewActions.click())

        // Espresso cannot directly read Toasts → we check indirectly via decorView
        // You’ll need to set up ToastMatcher
        Espresso
            .onView(ViewMatchers.withText(CoreMatchers.containsString("Email and Password required")))
            .inRoot(ToastMatcher())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        scenario.close()
    }
    // (Android Developers, n.d.)
}
