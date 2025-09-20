package com.example.groupflow.ui.ultrascans

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.groupflow.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UploadUltrascanActivityEspressoTest {
    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun clickingUploadButton_launchesFilePicker() {
        val scenario = ActivityScenario.launch(UploadUltrascanActivity::class.java)
        onView(withId(R.id.uploadImageButton)).perform(click())
        Intents.intended(hasAction(Intent.ACTION_GET_CONTENT))
    }
    // (Android Developers, n.d.)
}
