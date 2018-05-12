package com.egco.mailbot.dao

data class TokenResponse(
        val accessToken: String,
        val refreshToken: String
)