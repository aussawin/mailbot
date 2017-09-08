package com.egco.mailbot.controller

import com.egco.mailbot.dao.CallingReq
import com.egco.mailbot.dao.LogForm
import com.egco.mailbot.domain.Log
import com.egco.mailbot.repository.LogRepository
import com.egco.mailbot.repository.UserRepository
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping(value = "/api/controller")
@CrossOrigin("*")
class BotController(val userRepository: UserRepository,
                    val logRepository: LogRepository) {

    @RequestMapping(value = "/call", method = arrayOf(RequestMethod.POST))
    fun calling(@RequestBody req: CallingReq): String{
        val log: Log = Log(req.sender, req.target, req.subject, req.note, Date())
        logRepository.save(log)
        return "Success"
    }

    @RequestMapping(value = "/history", method = arrayOf(RequestMethod.GET))
    fun showHistory(): ArrayList<LogForm>{
        val log: ArrayList<Log> = logRepository.findBySenderContainingOrderByCreatedAt("user")!!
        val logList: ArrayList<LogForm> = ArrayList()
        log.mapTo(logList) { LogForm(it.sender, it.target, it.subject, it.note) }
        return logList
    }
}