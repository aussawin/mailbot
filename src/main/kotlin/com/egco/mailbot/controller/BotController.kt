package com.egco.mailbot.controller

import com.egco.mailbot.dao.CallingReq
import com.egco.mailbot.dao.HistoryForm
import com.egco.mailbot.dao.LogShow
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
    fun calling(@RequestBody req: CallingReq): LogShow{
        val log: Log = Log(req.sender, req.target, req.subject, req.note, Date())
        logRepository.save(log)
        val logged: LogShow = LogShow(log.sender, log.target, log.subject, log.note, log.createdAt)
        return logged
    }

    @RequestMapping(value = "/history", method = arrayOf(RequestMethod.GET))
    fun showHistory(): ArrayList<HistoryForm>{
        val log: ArrayList<Log> = logRepository.findBySenderContainingOrderByCreatedAt("user")!!
        val historyList: ArrayList<HistoryForm> = ArrayList()
        log.mapTo(historyList) { HistoryForm(it.sender, it.target, it.subject, it.note) }
        return historyList
    }
}