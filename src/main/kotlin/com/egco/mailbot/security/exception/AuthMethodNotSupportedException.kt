package com.egco.mailbot.security.exception

import org.springframework.security.authentication.AuthenticationServiceException

class AuthMethodNotSupportedException(msg: String) : AuthenticationServiceException(msg) {
    companion object {
        private val serialVersionUID = 3705043083010304496L
    }
}