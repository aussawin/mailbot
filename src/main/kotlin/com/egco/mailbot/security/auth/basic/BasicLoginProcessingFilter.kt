package com.egco.mailbot.security.auth.basic

import com.egco.mailbot.dao.RequestLogin
import com.egco.mailbot.security.exception.AuthMethodNotSupportedException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class BasicLoginProcessingFilter(defaultProcessUrl: String, @get:JvmName("getSuccessHandler_") private val successHandler: AuthenticationSuccessHandler, @get:JvmName("getFailureHandler_") private val failureHandler: AuthenticationFailureHandler, private val objectMapper: ObjectMapper) : AbstractAuthenticationProcessingFilter(defaultProcessUrl) {

    @Throws(AuthenticationException::class, IOException::class, ServletException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        if (HttpMethod.POST.name != request.method) {
            throw AuthMethodNotSupportedException("Authentication method not supported")
        }
        val (username, password) = objectMapper.readValue(request.reader, RequestLogin::class.java)
        if (StringUtils.isBlank(username)) {
            throw AuthenticationServiceException("Username is not proviceded")
        }
        if (StringUtils.isBlank(password)) {
            throw AuthenticationServiceException("Username is not proviceded")
        }

        val token = UsernamePasswordAuthenticationToken(username, password)
        return this.authenticationManager.authenticate(token)
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain?,
                                          authResult: Authentication) {
        successHandler.onAuthenticationSuccess(request, response, authResult)
    }

    @Throws(IOException::class, ServletException::class)
    override fun unsuccessfulAuthentication(request: HttpServletRequest, response: HttpServletResponse,
                                            failed: AuthenticationException) {
        SecurityContextHolder.clearContext()
        failureHandler.onAuthenticationFailure(request, response, failed)
    }

}