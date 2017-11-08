package com.egco.mailbot.security.exception

import org.springframework.security.core.AuthenticationException

class UsernameNotFoundException : AuthenticationException {

    constructor(msg: String) : super(msg) {}

    constructor(msg: String, t: Throwable) : super(msg, t) {}
}