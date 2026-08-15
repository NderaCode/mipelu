package com.cocido.mipelu

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.cocido.mipelu.core.ui.TestTags
import com.cocido.mipelu.data.local.fake.SeedData

/** SplashScreen has a 900ms auto-navigate delay; its root Box is also clickable to skip it. */
fun ComposeTestRule.skipSplash() {
    onRoot().performClick()
    waitForIdle()
}

/** From a fresh (logged-out) app start: skip Splash, go to Login, sign in as the seeded demo user. */
fun ComposeTestRule.loginAsDemoUser() {
    skipSplash() // lands on Onboarding
    onNodeWithText("Iniciar sesión").performClick()
    waitForIdle()
    onNodeWithTag(TestTags.LOGIN_EMAIL_FIELD).performTextInput(SeedData.DEMO_EMAIL)
    onNodeWithTag(TestTags.LOGIN_PASSWORD_FIELD).performTextInput(SeedData.DEMO_PASSWORD)
    onNodeWithTag(TestTags.LOGIN_SUBMIT_BUTTON).performClick()
    waitForIdle()
}
