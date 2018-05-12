package com.egco.mailbot.security.exception

import org.springframework.security.core.AuthenticationException

class InvalidJwtToken : AuthenticationException {

    constructor(msg: String, t: Throwable) : super(msg, t) {}

    constructor(msg: String) : super(msg) {}

    companion object {
        private val serialVersionUID = -294671188037098603L
    }
}