package com.egco.mailbot.config

import org.springframework.context.annotation.Configuration

@Configuration
class RaspConfig {
    val baseUrl: String = "http://172.20.10.10:3000/call"
}