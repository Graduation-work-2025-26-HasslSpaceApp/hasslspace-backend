package ru.hse.hasslspace.userservice.controller

const val USER_ID_HEADER = "X-User-Id"

const val USER_SERVICE_BASE_PATH_URL = "/api/user-service"

const val REGISTER_USER_URL = "/auth/register"
const val LOGIN_USER_URL = "/auth/login"
const val SEND_VERIFICATION_CODE_URL = "/users/me/verification"
const val VERIFY_CODE_URL = "/users/me/verification/confirm"
const val IS_VERIFIED_URL = "/users/me/verification/status"

const val GET_PROFILE_URL = "/users/me"
const val UPDATE_PROFILE_URL = "/users/me"
const val UPDATE_STATUS_URL = "/users/me/status"

const val GET_USER_PROFILE_URL = "/users/profile"
const val GET_FRIENDS_URL = "/users/me/friends"
const val FRIEND_REQUESTS_URL = "/users/me/friends"
const val FRIEND_RESPONSE_URL = "/friends/me/response"
const val BLOCK_USER_URL = "/users/block"
const val DELETE_USER_URL = "/users/me/friends/delete"

const val UPLOAD_PHOTO_URL = "/photos/user/upload"
const val DOWNLOAD_PHOTO_URL = "/photos/user/download"