package com.cocido.mipelu.feature.clients

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
class NewClientFormTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.loginAsDemoUser()
        composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_CLIENTS).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(TestTags.FAB_ADD).performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun blankNameBlocksSaveAndShowsAValidationMessage() {
        composeRule.onNodeWithTag(TestTags.NEW_CLIENT_SAVE_BUTTON).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Ingresá al menos el nombre de la clienta.").assertExists()
    }

    @Test
    fun savingWithAValidNameReturnsToTheListWithTheNewClientVisible() {
        composeRule.onNodeWithTag(TestTags.NEW_CLIENT_NAME_FIELD).performTextInput("Valentina Rossi")
        composeRule.onNodeWithTag(TestTags.NEW_CLIENT_SAVE_BUTTON).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Valentina Rossi").assertExists()
    }
}
