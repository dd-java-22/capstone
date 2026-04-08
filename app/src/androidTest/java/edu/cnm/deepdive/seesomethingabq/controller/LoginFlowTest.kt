package edu.cnm.deepdive.seesomethingabq.controller

import androidx.navigation.fragment.NavHostFragment
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
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
        val scenario = ActivityScenario.launch(UserWorkflowActivity::class.java)

        // Then: User Dashboard should be displayed (either via auto-login or clicking if needed)
        // We verify that the NavController reached the dashboard destination.
        val latch = CountDownLatch(1)
        scenario.onActivity { activity ->
            val navHostFragment = activity.supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
            val navController = navHostFragment.navController
            if (navController.currentDestination?.id == R.id.user_dashboard_fragment) {
                latch.countDown()
            } else {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    if (destination.id == R.id.user_dashboard_fragment) {
                        latch.countDown()
                    }
                }
            }
        }

        assertTrue("Timed out waiting for navigation to User Dashboard", latch.await(5, TimeUnit.SECONDS))
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
