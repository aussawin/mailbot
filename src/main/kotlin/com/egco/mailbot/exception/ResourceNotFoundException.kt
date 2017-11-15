package com.egco.mailbot.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.NOT_FOUND, reason = "Resource not found.")
class ResourceNotFoundException(message: String = "Resource not found.") : RuntimeException(message)