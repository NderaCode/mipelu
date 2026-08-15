package com.cocido.mipelu.feature.auth

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.cocido.mipelu.MainActivity
import com.cocido.mipelu.core.ui.TestTags
import com.cocido.mipelu.data.local.fake.SeedData
import com.cocido.mipelu.loginAsDemoUser
import com.cocido.mipelu.skipSplash
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun validCredentialsNavigateToHome() {
        composeRule.loginAsDemoUser()

        composeRule.onNodeWithText("Hola, Romina").assertExists()
    }

    @Test
    fun wrongPasswordShowsAnErrorAndStaysOnLogin() {
        composeRule.skipSplash() // lands on Onboarding
        composeRule.onNodeWithText("Iniciar sesión").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTags.LOGIN_EMAIL_FIELD).performTextInput(SeedData.DEMO_EMAIL)
        composeRule.onNodeWithTag(TestTags.LOGIN_PASSWORD_FIELD).performTextInput("wrong-password")
        composeRule.onNodeWithTag(TestTags.LOGIN_SUBMIT_BUTTON).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTags.LOGIN_ERROR_MESSAGE).assertExists()
    }
}
