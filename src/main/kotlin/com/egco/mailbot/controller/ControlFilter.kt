package com.egco.mailbot.controller

import com.egco.mailbot.security.config.AuthProperties
import com.egco.mailbot.security.model.token.RawAccessJwtToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.logging.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ControllerFilter: javax.servlet.Filter {

    private var filterConfig: FilterConfig? = null

    @Autowired
    lateinit var properties: AuthProperties


    override fun init(filterConfig: FilterConfig?) {
        this.filterConfig = filterConfig
    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {

        val servletRequest: HttpServletRequest = request as HttpServletRequest
        val servletResponse: HttpServletResponse = response as HttpServletResponse

        if(servletRequest.requestURI.indexOf("/api/challenges")>=0) {
            servletResponse.setHeader("X-Token-Expired-Time", getExpToken(Date(), request.getHeader("X-Authorization")).toString())
        }
        chain!!.doFilter(servletRequest, servletResponse)
    }

    override fun destroy() {
        filterConfig = null
    }

    fun getExpToken(date: Date, token: String): Long{
        val rawToken = RawAccessJwtToken(token)
        val jwsClaims = rawToken.parseClaims(this.properties.signingKey)
        val tokenExp: Date = jwsClaims.body.expiration
        val dateNow = Date()
        return (tokenExp.time - dateNow.time) / 1000
    }
}