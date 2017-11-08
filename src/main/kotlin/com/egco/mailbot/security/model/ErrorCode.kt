package com.egco.mailbot.security.model

enum class ErrorCode private constructor(val errorCode: Int) {
    GLOBAL(2),

    AUTHENTICATION(10), JWT_TOKEN_EXPIRED(11), INVALID_JWT_TOKEN(12)
}
