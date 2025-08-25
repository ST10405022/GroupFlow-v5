package com.example.groupflow.ui.ultrascans

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.groupflow.R
import com.example.groupflow.ToastMatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UltrascansActivityTest {

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun topAppBarBack_clickFinishesActivity() {
        val scenario = ActivityScenario.launch(UltrascansActivity::class.java)
        onView(withId(R.id.topAppBarUltrascans)).perform(click())
        scenario.onActivity { activity ->
            assert(activity.isFinishing || activity.isDestroyed)
        }
    }
    //(Android Developers, n.d.)
    @Test
    fun viewButton_noFile_showsToast() {
        ActivityScenario.launch(UltrascansActivity::class.java)
        onView(withId(R.id.btnView)).perform(click())
        onView(withText("No file selected"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun downloadButton_noFile_showsToast() {
        ActivityScenario.launch(UltrascansActivity::class.java)
        onView(withId(R.id.btnDownload)).perform(click())
        onView(withText("No file selected"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun viewButton_withFile_sendsIntent() {
        val scenario = ActivityScenario.launch(UltrascansActivity::class.java)

        scenario.onActivity { activity ->
            activity.currentFileUrl = "https://example.com/file.pdf"
        }

        onView(withId(R.id.btnView)).perform(click())

        Intents.intended(
            IntentMatchers.hasAction(Intent.ACTION_VIEW)
        )
        Intents.intended(
            IntentMatchers.hasData(Uri.parse("https://example.com/file.pdf"))
        )
    }

    @Test
    fun downloadButton_withFile_showsDownloadingToast() {
        val scenario = ActivityScenario.launch(UltrascansActivity::class.java)
        scenario.onActivity { activity ->
            activity.currentFileUrl = "https://example.com/file.pdf"
        }

        onView(withId(R.id.btnDownload)).perform(click())
        onView(withText("Downloading..."))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }
    //(Android Developers, n.d.)
}
