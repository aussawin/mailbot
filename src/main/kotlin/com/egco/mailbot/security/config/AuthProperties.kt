package com.egco.mailbot.security.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "mailbot.security")
class AuthProperties {
    lateinit var signingKey: String
    lateinit var Issuer: String
    lateinit var tokenExpirationTime: String
    lateinit var refreshTokenExpTime: String
}