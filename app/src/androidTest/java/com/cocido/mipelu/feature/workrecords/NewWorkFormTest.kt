package com.cocido.mipelu.feature.workrecords

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import com.cocido.mipelu.MainActivity
import com.cocido.mipelu.core.ui.TestTags
import com.cocido.mipelu.loginAsDemoUser
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NewWorkFormTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.loginAsDemoUser()
        composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_WORKS).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(TestTags.FAB_ADD).performClick()
        composeRule.waitForIdle()
    }

    private fun scrollFormTo(tag: String) {
        composeRule.onNodeWithTag(TestTags.NEW_WORK_FORM_LIST)
            .performScrollToNode(hasTestTag(tag))
        composeRule.waitForIdle()
    }

    private fun scrollFormToText(text: String) {
        composeRule.onNodeWithTag(TestTags.NEW_WORK_FORM_LIST)
            .performScrollToNode(hasText(text))
        composeRule.waitForIdle()
    }

    @Test
    fun saveButtonStaysDisabledUntilAClientAndAServiceTypeAreChosen() {
        // Client selector and service type chips are near the top (visible without scrolling);
        // Guardar is the very last item in a long LazyColumn - only compose/interact with each
        // section in the order they'd actually scroll past, since LazyColumn only composes what's
        // in (or near) the viewport.
        composeRule.onNodeWithTag(TestTags.NEW_WORK_CLIENT_SEARCH_FIELD).performTextInput("Ana")
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Ana Fernández").performClick()
        composeRule.waitForIdle()

        scrollFormTo(TestTags.NEW_WORK_SAVE_BUTTON)
        composeRule.onNodeWithTag(TestTags.NEW_WORK_SAVE_BUTTON).assertIsNotEnabled() // still no service type

        scrollFormToText("Color")
        composeRule.onNodeWithText("Color").performClick()
        composeRule.waitForIdle()

        scrollFormTo(TestTags.NEW_WORK_SAVE_BUTTON)
        composeRule.onNodeWithTag(TestTags.NEW_WORK_SAVE_BUTTON).assertIsEnabled()
    }

    @Test
    fun beforePhotoSlotIsTappableWithoutCrashing() {
        // The Photo Picker itself runs out-of-process (a separate system Activity, with
        // unpredictable resumed/backgrounded behavior across emulator images) and isn't testable
        // in-process. This only verifies the launcher wiring (rememberPhotoPickerLauncher) doesn't
        // crash when the slot is tapped - it deliberately doesn't interact with anything after,
        // since the tap backgrounds this app in favor of the system picker.
        scrollFormTo(TestTags.NEW_WORK_BEFORE_PHOTO_SLOT)
        composeRule.onNodeWithTag(TestTags.NEW_WORK_BEFORE_PHOTO_SLOT).performClick()
    }
}
