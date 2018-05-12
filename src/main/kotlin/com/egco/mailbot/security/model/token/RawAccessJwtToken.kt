package com.egco.mailbot.security.model.token

import com.egco.mailbot.security.exception.JwtExpiredTokenException
import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException

class RawAccessJwtToken(override val token: String) : JwtToken {

    /**
     * Parses and validates JWT Token signature.

     * @throws BadCredentialsException
     * *
     * @throws JwtExpiredTokenException
     */
    fun parseClaims(signingKey: String): Jws<Claims> {
        try {
            return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token)
        } catch (ex: UnsupportedJwtException) {
            logger.warn("Invalid JWT Token {}, {}", ex.message, this.token)
            throw BadCredentialsException("Invalid JWT token: ", ex)
        } catch (ex: MalformedJwtException) {
            logger.warn("Invalid JWT Token {}, {}", ex.message, this.token)
            throw BadCredentialsException("Invalid JWT token: ", ex)
        } catch (ex: IllegalArgumentException) {
            logger.warn("Invalid JWT Token {}, {}", ex.message, this.token)
            throw BadCredentialsException("Invalid JWT token: ", ex)
        } catch (ex: SignatureException) {
            logger.warn("Invalid JWT Token {}, {}", ex.message, this.token)
            throw BadCredentialsException("Invalid JWT token: ", ex)
        } catch (expiredEx: ExpiredJwtException) {
            logger.debug("JWT Token is expired, {}", this.token)
            throw JwtExpiredTokenException(this, "JWT Token expired", expiredEx)
        }

    }

    companion object {

        private val logger = LoggerFactory.getLogger(RawAccessJwtToken::class.java)
    }
}