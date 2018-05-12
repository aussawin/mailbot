package com.egco.mailbot.security.auth.jwt.extractor

import org.apache.commons.lang3.StringUtils
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Component

@Component
class JwtHeaderTokenExtractor : TokenExtractor {

    override fun extract(payload: String): String {
        if (StringUtils.isBlank(payload)) {
            throw AuthenticationServiceException("Authorization header cannot be blank!")
        }

        if (payload.length < HEADER_PREFIX.length) {
            throw AuthenticationServiceException("Invalid authorization header size.")
        }

        return payload.substring(HEADER_PREFIX.length, payload.length)
    }

    companion object {

        private val HEADER_PREFIX = "Bearer "
    }
}