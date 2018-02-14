package com.egco.mailbot.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class RaspStatusError(message: String = "Raspberry command's return ",
                      status: String = "error") : Exception(message + status)