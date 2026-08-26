package com.cocido.mipelu.core.ui

/**
 * Centralized testTag constants so Screen and instrumented-test code reference the same string
 * literal instead of duplicating it on both sides. Dynamic tags (list items) are functions.
 */
object TestTags {
    // Bottom nav (MainActivity's Scaffold FAB + MiPeluBottomNavBar)
    const val BOTTOM_NAV_HOME = "bottom_nav_home"
    const val BOTTOM_NAV_CLIENTS = "bottom_nav_clients"
    const val BOTTOM_NAV_WORKS = "bottom_nav_works"
    const val BOTTOM_NAV_PROFILE = "bottom_nav_profile"
    const val FAB_ADD = "fab_add"

    // Login
    const val LOGIN_EMAIL_FIELD = "login_email_field"
    const val LOGIN_PASSWORD_FIELD = "login_password_field"
    const val LOGIN_SUBMIT_BUTTON = "login_submit_button"
    const val LOGIN_SIGNUP_BUTTON = "login_signup_button"
    const val LOGIN_ERROR_MESSAGE = "login_error_message"

    // Home
    const val HOME_ERROR_RETRY = "home_error_retry"

    // Client list
    const val CLIENT_LIST_SEARCH_FIELD = "client_list_search_field"
    const val CLIENT_LIST_ERROR_RETRY = "client_list_error_retry"
    fun clientListItem(clientId: String) = "client_list_item_$clientId"

    // New client
    const val NEW_CLIENT_NAME_FIELD = "new_client_name_field"
    const val NEW_CLIENT_SAVE_BUTTON = "new_client_save_button"
    const val NEW_CLIENT_LIMIT_NOTICE = "new_client_limit_notice"

    // Work list
    const val WORK_LIST_SEARCH_FIELD = "work_list_search_field"
    const val WORK_LIST_ERROR_RETRY = "work_list_error_retry"
    fun workListItem(workId: String) = "work_list_item_$workId"

    // New work
    const val NEW_WORK_FORM_LIST = "new_work_form_list"
    const val NEW_WORK_CLIENT_SEARCH_FIELD = "new_work_client_search_field"
    const val NEW_WORK_SAVE_BUTTON = "new_work_save_button"
    const val NEW_WORK_BEFORE_PHOTO_SLOT = "new_work_before_photo_slot"
    const val NEW_WORK_AFTER_PHOTO_SLOT = "new_work_after_photo_slot"
}
