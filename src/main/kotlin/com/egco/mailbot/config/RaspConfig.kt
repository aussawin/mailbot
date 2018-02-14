package com.egco.mailbot.config

import org.springframework.context.annotation.Configuration

@Configuration
class RaspConfig {
    final val baseUrl: String = "http://172.20.10.10:3000"
    val CALL : String = baseUrl + "/call"
    val SEND : String = baseUrl + "/send"
    val UNLOCK : String = baseUrl + "/unlock"
    val COUNTDOWN : String = baseUrl + "/countdown"
    val OPENCAM : String = baseUrl + "/opencam"
}