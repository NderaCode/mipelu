package com.cocido.mipelu

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.cocido.mipelu.core.ui.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Bottom nav tab switching and the FAB's per-destination visibility (MainActivity's fabAction). */
@HiltAndroidTest
class NavigationSmokeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.loginAsDemoUser()
    }

    @Test
    fun bottomNavSwitchesBetweenTheFourTopLevelDestinations() {
        composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_CLIENTS).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(TestTags.CLIENT_LIST_SEARCH_FIELD).assertExists()

        composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_WORKS).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(TestTags.WORK_LIST_SEARCH_FIELD).assertExists()

        composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_PROFILE).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_HOME).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(TestTags.FAB_ADD).assertDoesNotExist()
    }

    @Test
    fun fabOnlyShowsUpOnClientListAndWorkList() {
        composeRule.onNodeWithTag(TestTags.FAB_ADD).assertDoesNotExist() // Home: no FAB

        composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_CLIENTS).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(TestTags.FAB_ADD).assertExists()

        composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_WORKS).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(TestTags.FAB_ADD).assertExists()

        composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_PROFILE).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(TestTags.FAB_ADD).assertDoesNotExist() // Perfil: no FAB
    }
}
