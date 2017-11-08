package com.egco.mailbot.security.auth.jwt

import com.egco.mailbot.domain.User
import com.egco.mailbot.repository.UserRepository
import com.egco.mailbot.security.JwtAuthenticationToken
import com.egco.mailbot.security.config.AuthProperties
import com.egco.mailbot.security.exception.InvalidJwtToken
import com.egco.mailbot.security.model.token.RawAccessJwtToken
import com.egco.mailbot.security.model.token.TokenType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider @Autowired
constructor(private val userRepository: UserRepository, private val properties: AuthProperties) : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val rawAccessJwtToken = authentication.credentials as RawAccessJwtToken
        val jwsClaims = rawAccessJwtToken.parseClaims(properties.signingKey)
        val tokenType = jwsClaims.body.get("type", String::class.java)
        if (tokenType == null || tokenType != TokenType.ACCESS_TOKEN.toString()) {
            throw InvalidJwtToken("Invalid token type")
        }

        val subject = jwsClaims.body.subject
        val user: User = userRepository.findByUsername(subject) ?: throw InvalidJwtToken("User not found" + subject)
        val authorities = HashSet<GrantedAuthority>()
        SimpleGrantedAuthority("user")

        return JwtAuthenticationToken(user, authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}