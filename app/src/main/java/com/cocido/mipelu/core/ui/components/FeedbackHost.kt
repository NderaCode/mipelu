package com.cocido.mipelu.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * One place for transient action feedback (delete failed, save failed from a detail screen)
 * instead of a mix of Toast, inline Text and nothing. Validation errors on a form still live
 * next to the field - those aren't transient.
 */
@Composable
fun FeedbackHost(
    message: String?,
    onConsumed: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val hostState = remember { SnackbarHostState() }
    LaunchedEffect(message) {
        if (message != null) {
            hostState.showSnackbar(message)
            onConsumed()
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        content()
        SnackbarHost(
            hostState = hostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
        )
    }
}
