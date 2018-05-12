package com.egco.mailbot.controller

import com.egco.mailbot.dao.TokenResponse
import com.egco.mailbot.repository.UserRepository
import com.egco.mailbot.security.config.AuthProperties
import com.egco.mailbot.security.config.WebSecurityConfig.Companion.JWT_REFRESH_TOKEN_HEADER_PARAM
import com.egco.mailbot.security.exception.InvalidJwtToken
import com.egco.mailbot.security.model.token.RawAccessJwtToken
import com.egco.mailbot.security.model.token.TokenType
import com.egco.mailbot.security.service.JwtTokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController
@Autowired
constructor(private val tokenService: JwtTokenService,
            private val userRepository: UserRepository,
            private val passwordEncoder: PasswordEncoder,
            private val properties: AuthProperties){

    @RequestMapping(value = "/api/auth/refreshToken", method = arrayOf(RequestMethod.POST))
    fun refreshToken(@RequestHeader(value = JWT_REFRESH_TOKEN_HEADER_PARAM) jwtHeader: String): TokenResponse {

        val rawToken = RawAccessJwtToken(jwtHeader)
        val claims = rawToken.parseClaims(properties.signingKey)
        val tokenType = claims.body.get("type", String::class.java)
        if (tokenType == null || tokenType != TokenType.REFRESH_TOKEN.toString()) {
            throw InvalidJwtToken("Invalid token type")
        }
        val subject = claims.body.subject
        val user = userRepository.findByUsername(subject) ?: throw InvalidJwtToken("User not found:" + subject)

        val access = tokenService.createAccessJwtToken(user)
        val refresh = tokenService.createRefreshToken(user)

        val tokenResponse = TokenResponse(access.token, refresh.token)
        return tokenResponse
    }
}