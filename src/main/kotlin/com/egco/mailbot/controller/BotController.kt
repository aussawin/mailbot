package com.egco.mailbot.controller

import com.egco.mailbot.config.RaspConfig
import com.egco.mailbot.dao.CallingReqire
import com.egco.mailbot.dao.LogTemplate
import com.egco.mailbot.domain.Log
import com.egco.mailbot.domain.User
import com.egco.mailbot.repository.LogRepository
import com.egco.mailbot.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

@RestController
@RequestMapping(value = "/api/controller")
@CrossOrigin("*")
class BotController(val userRepository: UserRepository,
                    val logRepository: LogRepository,
                    val raspConfig: RaspConfig) {

    @RequestMapping(value = "/call", method = arrayOf(RequestMethod.POST))
    fun calling(@RequestBody req: CallingReqire): String{
        val sender = SecurityContextHolder.getContext().authentication.principal as User
        val target = userRepository.findByName(req.target)

//        val tmpInt: TemplateInt = TemplateInt(1)
//        val restTemplate: RestTemplate = RestTemplate()
//        var res: String = restTemplate.postForObject(raspConfig.baseUrl, tmpInt, String::class.java)

        val status = if (logRepository.findByStatusOrderByCreatedAt("calling")!!.isEmpty()) { "calling" }
        else{ "wait" }

        val log = Log(sender.name, sender.location, target!!.name, target.location, req.subject, req.note, status, Date())
        logRepository.save(log)
        return "Success"
    }
    
    @RequestMapping(value = "/history", method = arrayOf(RequestMethod.GET))
    fun showHistory(): ArrayList<LogTemplate>{
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val log: ArrayList<Log> = logRepository.findBySenderOrderByCreatedAt(user.name)!!
        val logList: ArrayList<LogTemplate> = ArrayList()
        log.mapTo(logList) { LogTemplate(it.sender, it.target, it.subject, it.note, it.createdAt, it.status) }
        return logList
    }

    @RequestMapping(value = "/queue", method = arrayOf(RequestMethod.GET))
    fun showQueue(): ArrayList<LogTemplate>{
        val log: ArrayList<Log> = logRepository.findByStatusOrderByCreatedAt("wait")!!
        val logList: ArrayList<LogTemplate> = ArrayList()
        log.mapTo(logList) { LogTemplate(it.sender, it.target, it.subject, it.note, it.createdAt, it.status) }
        return logList
    }

    @RequestMapping(value = "/changeStatus", method = arrayOf(RequestMethod.PATCH))
    fun changeStatus(@RequestBody req: LogTemplate) {
        val callLog = logRepository.findByStatus("calling")
        if (callLog != null) {
            callLog.status = "done"
            logRepository.save(callLog)
        }
        val waitList = logRepository.findByStatusOrderByCreatedAt("wait")
        if (waitList!!.isNotEmpty()){
            waitList[0].status = "calling"
            logRepository.save(waitList)
        }
    }
}