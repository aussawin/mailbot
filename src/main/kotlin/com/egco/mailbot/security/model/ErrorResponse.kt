package com.egco.mailbot.security.model

import org.springframework.http.HttpStatus

class ErrorResponse protected constructor(// General Error message
        val message: String, // Error code
        val error: ErrorCode, // HTTP Response Status Code
        private val code: HttpStatus) {

    fun getCode(): Int? {
        return code.value()
    }

    companion object {

        fun of(message: String, error: ErrorCode, code: HttpStatus): ErrorResponse {
            return ErrorResponse(message, error, code)
        }
    }
}