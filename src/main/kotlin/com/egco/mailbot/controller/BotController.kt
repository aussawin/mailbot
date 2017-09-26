package com.egco.mailbot.controller

import com.egco.mailbot.config.RaspConfig
import com.egco.mailbot.dao.CallingReq
import com.egco.mailbot.dao.LogForm
import com.egco.mailbot.domain.Log
import com.egco.mailbot.repository.LogRepository
import com.egco.mailbot.repository.UserRepository
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.util.*

@RestController
@RequestMapping(value = "/api/controller")
@CrossOrigin("*")
class BotController(val userRepository: UserRepository,
                    val logRepository: LogRepository,
                    val raspConfig: RaspConfig) {

    @RequestMapping(value = "/call", method = arrayOf(RequestMethod.POST))
    fun calling(@RequestBody req: CallingReq): String{
        val loc: Int = 1
        val log: Log = Log(req.sender, loc, req.target, loc, req.subject, req.note, "Calling", Date())
        logRepository.save(log)

        var tmpInt: TemplateInt = TemplateInt(1)

        var restTemplate: RestTemplate = RestTemplate()
        var retsult: TemplateInt = restTemplate.postForObject(raspConfig.baseUrl, tmpInt, TemplateInt::class.java)

        return "Success"
    }
    
    @RequestMapping(value = "/history", method = arrayOf(RequestMethod.GET))
    fun showHistory(): ArrayList<LogForm>{
        val log: ArrayList<Log> = logRepository.findBySenderContainingOrderByCreatedAt("user")!!
        val logList: ArrayList<LogForm> = ArrayList()
        log.mapTo(logList) { LogForm(it.sender, it.target, it.subject, it.note, it.updatedAt, it.status) }
        return logList
    }
}
data class TemplateInt(var i: Int)