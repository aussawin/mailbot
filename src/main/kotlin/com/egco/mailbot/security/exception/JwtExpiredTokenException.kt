package com.egco.mailbot.security.exception

import com.egco.mailbot.security.model.token.JwtToken
import org.springframework.security.core.AuthenticationException

class JwtExpiredTokenException : AuthenticationException {

    private var token: JwtToken? = null

    constructor(msg: String) : super(msg) {}

    constructor(token: JwtToken, msg: String, t: Throwable) : super(msg, t) {
        this.token = token
    }

    fun token(): String {
        return this.token!!.token
    }

    companion object {
        private val serialVersionUID = -5959543783324224864L
    }
}