package com.cocido.mipelu.domain.repository

/**
 * Whether we know yet if there's a usable session.
 *
 * [Restoring] exists because answering "is this user logged in?" at cold start requires a network
 * round trip (the stored token has to be validated against the backend). Without an explicit
 * "don't know yet" state, callers are forced to read `currentUser == null` while that request is
 * still in flight and conclude "logged out", which is how a returning user ends up staring at the
 * onboarding screen.
 */
enum class SessionState {
    Restoring,
    Authenticated,
    Unauthenticated,
}
