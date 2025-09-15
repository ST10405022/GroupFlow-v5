package com.example.groupflow.ui.reviews

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
class ReviewsActivityTest {

    @Test
    fun testUiElementsAreDisplayed() {
        ActivityScenario.launch(ReviewsActivity::class.java)

        // Check that recycler view is displayed
        onView(withId(R.id.recyclerReviews))
            .check(matches(isDisplayed()))

        // Check that Add Review button (FAB) is displayed
        onView(withId(R.id.fabAddReview))
            .check(matches(isDisplayed()))

        // Check that bottom navigation is visible
        onView(withId(R.id.bottomNav))
            .check(matches(isDisplayed()))

        // Check that the top app bar is visible
        onView(withId(R.id.topAppBarReviews))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testFabAddReviewOpensLeaveReviewActivity() {
        ActivityScenario.launch(ReviewsActivity::class.java)

        // Click the Add Review button
        onView(withId(R.id.fabAddReview)).perform(click())

        // Verify new screen loads (LeaveReviewActivity should have a rating bar)
        onView(withId(R.id.ratingBar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBottomNavigationIsInteractive() {
        ActivityScenario.launch(ReviewsActivity::class.java)

        // Click on Appointments tab
        onView(withId(R.id.nav_appointments)).perform(click())
        // No crash means success; further assertions depend on AppointmentsActivity UI
    }
}
