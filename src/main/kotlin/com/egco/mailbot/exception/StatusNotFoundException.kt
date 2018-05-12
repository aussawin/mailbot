package com.egco.mailbot.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "Status request not found")
class StatusNotFoundException(message : String = "Status request not found",
                              val status: String) : Exception(message)