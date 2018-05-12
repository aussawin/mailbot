package com.egco.mailbot.security.auth.basic

import com.egco.mailbot.dao.TokenResponse
import com.egco.mailbot.domain.User
import com.egco.mailbot.security.service.JwtTokenService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class BasicAwareAuthenticationSuccessHandler @Autowired
constructor(private val mapper: ObjectMapper, private val tokenService: JwtTokenService) : AuthenticationSuccessHandler {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        val user = authentication.principal as User
        val accessToken = tokenService.createAccessJwtToken(user)
        val refreshToken = tokenService.createRefreshToken(user)
        val tokenResponse = TokenResponse(accessToken.token, refreshToken.token)
        response.status = HttpStatus.OK.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        mapper.writeValue(response.writer, tokenResponse)
        clearAuthenticationAttributes(request)
    }

    protected fun clearAuthenticationAttributes(request: HttpServletRequest) {
        val session = request.getSession(false) ?: return

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)
    }
}