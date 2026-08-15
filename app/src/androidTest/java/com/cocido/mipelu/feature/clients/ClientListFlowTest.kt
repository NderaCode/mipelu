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
class ClientListFlowTest {

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
    }

    @Test
    fun seededClientsAreListed() {
        composeRule.onNodeWithTag(TestTags.clientListItem("client-1")).assertExists()
        composeRule.onNodeWithTag(TestTags.clientListItem("client-2")).assertExists()
        composeRule.onNodeWithTag(TestTags.clientListItem("client-3")).assertExists()
    }

    @Test
    fun searchFiltersTheListByName() {
        composeRule.onNodeWithTag(TestTags.CLIENT_LIST_SEARCH_FIELD).performTextInput("Lucía")
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTags.clientListItem("client-2")).assertExists() // Lucía Martínez
        composeRule.onNodeWithTag(TestTags.clientListItem("client-1")).assertDoesNotExist()
    }

    @Test
    fun fabOpensTheNewClientForm() {
        composeRule.onNodeWithTag(TestTags.FAB_ADD).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTags.NEW_CLIENT_NAME_FIELD).assertExists()
    }
}
