package com.egco.mailbot.config

import org.springframework.context.annotation.Configuration

@Configuration
class RaspConfig {
    final val baseUrl: String = "http://192.168.1.126:3000"
    val CALL : String           = baseUrl + "/call"
    val UNLOCK : String         = baseUrl + "/unlock"
    val COUNTDOWN : String      = baseUrl + "/countdown"
    val OPENCAM : String        = baseUrl + "/opencam"
    val WAIT_FOR_PRESS : String = baseUrl + "/waitforpress"
    val CHECK_POINT : String    = baseUrl + "/checkpoint"

    val STATUS_OK = "okay"
    val STATUS_FAILED = "failed"
}