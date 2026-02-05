package ru.hse.hasslspace.userservice.controller

const val USER_ID_HEADER = "X-User-Id"

const val USER_SERVICE_BASE_PATH_URL = "/api/user-service"

const val REGISTER_USER_URL = "/auth/register"
const val LOGIN_USER_URL = "/auth/login"
const val SEND_VERIFICATION_CODE_URL = "/users/me/verification"
const val VERIFY_CODE_URL = "/users/me/verification/confirm"
const val IS_VERIFIED_URL = "/users/me/verification/status"

