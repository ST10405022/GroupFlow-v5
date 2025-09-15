package com.example.groupflow.ui.reviews

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.groupflow.R
import com.example.groupflow.ToastMatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LeaveReviewActivityTest {

    private lateinit var scenario: ActivityScenario<LeaveReviewActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(Intent(Intent.ACTION_MAIN).setClassName(
            "com.example.groupflow",
            "com.example.groupflow.ui.reviews.LeaveReviewActivity"
        ))
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testUiElementsDisplayed() {
        onView(withId(R.id.ratingBar)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextReview)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSubmitReview)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyReviewShowsErrorMessage() {
        onView(withId(R.id.btnSubmitReview)).perform(click())

        onView(withText("Provide a comment and a rating"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }
}
