package com.egco.mailbot.config

import org.springframework.context.annotation.Configuration

@Configuration
class RaspConfig {
    val baseUrl: String = "http://192.168.43.245:3000/call"
}