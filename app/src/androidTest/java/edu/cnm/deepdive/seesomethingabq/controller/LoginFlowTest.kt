package edu.cnm.deepdive.seesomethingabq.controller

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.cnm.deepdive.seesomethingabq.R
import edu.cnm.deepdive.seesomethingabq.service.proxy.SeeSomethingWebService
import edu.cnm.deepdive.seesomethingabq.service.repository.FakeGoogleAuthRepository
import edu.cnm.deepdive.seesomethingabq.service.repository.FakeSeeSomethingWebService
import edu.cnm.deepdive.seesomethingabq.service.repository.GoogleAuthRepository
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class LoginFlowTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: GoogleAuthRepository

    @Inject
    lateinit var webService: SeeSomethingWebService

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun loginSuccess_navigatesToDashboard() {
        // Given: The fake repository is configured to succeed (default)
        val fakeRepository = repository as FakeGoogleAuthRepository
        fakeRepository.failSignIn = false

        val fakeWebService = webService as FakeSeeSomethingWebService
        fakeWebService.failGetMe = false

        // When: Launching the activity
        ActivityScenario.launch(UserWorkflowActivity::class.java)

        // Then: User Dashboard should be displayed (either via auto-login or clicking if needed)
        // We check for the TextView inside the ConstraintLayout of the dashboard
        onView(
            allOf(
                withText("User Dashboard"),
                withParent(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")))
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun loginFailure_staysOnLoginScreen() {
        // Given: The fake repository is configured to fail
        val fakeRepository = repository as FakeGoogleAuthRepository
        fakeRepository.failSignIn = true

        // When: Launching the activity
        ActivityScenario.launch(UserWorkflowActivity::class.java)

        // Manually click login button since signInQuickly will fail silently and we'll be left on login screen
        onView(withId(R.id.login_button)).perform(click())

        // Then: Login button should still be visible on the login screen
        onView(withId(R.id.login_button)).check(matches(isDisplayed()))
    }
}
