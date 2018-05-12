package com.egco.mailbot.controller

import com.egco.mailbot.dao.ResponseMessage
import com.egco.mailbot.exception.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ExceptionControllerAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException::class)
    fun resourceNotFoundExceptionHandler(e: ResourceNotFoundException): ResponseMessage {
        return ResponseMessage(HttpStatus.NOT_FOUND.value(), e.message!!)
    }

}