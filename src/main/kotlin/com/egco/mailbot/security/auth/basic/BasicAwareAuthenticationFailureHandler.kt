package com.egco.mailbot.security.auth.basic

import com.egco.mailbot.security.exception.AuthMethodNotSupportedException
import com.egco.mailbot.security.exception.InvalidJwtToken
import com.egco.mailbot.security.exception.JwtExpiredTokenException
import com.egco.mailbot.security.exception.UsernameNotFoundException
import com.egco.mailbot.security.model.ErrorCode
import com.egco.mailbot.security.model.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class BasicAwareAuthenticationFailureHandler @Autowired
constructor(private val mapper: ObjectMapper) : AuthenticationFailureHandler {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        when (exception) {
            is BadCredentialsException -> mapper.writeValue(response.writer, ErrorResponse.of(exception.message.toString(), ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED))
            is JwtExpiredTokenException -> mapper.writeValue(response.writer, ErrorResponse.of("Token has expired", ErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED))
            is AuthMethodNotSupportedException -> mapper.writeValue(response.writer, ErrorResponse.of(exception.message.toString(), ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED))
            is UsernameNotFoundException -> mapper.writeValue(response.writer, ErrorResponse.of(exception.message.toString(), ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED))
            is InvalidJwtToken -> mapper.writeValue(response.writer, ErrorResponse.of("Invalid token", ErrorCode.INVALID_JWT_TOKEN, HttpStatus.UNAUTHORIZED))
            is AuthenticationServiceException -> mapper.writeValue(response.writer, ErrorResponse.of(exception.message.toString(), ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED))
        }
    }
}