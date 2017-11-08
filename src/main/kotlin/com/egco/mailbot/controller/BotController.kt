package com.egco.mailbot.controller

import com.egco.mailbot.config.RaspConfig
import com.egco.mailbot.dao.CallingReq
import com.egco.mailbot.dao.LogForm
import com.egco.mailbot.domain.Log
import com.egco.mailbot.domain.User
import com.egco.mailbot.repository.LogRepository
import com.egco.mailbot.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
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
        val sender = SecurityContextHolder.getContext().authentication.principal as User
        val target = userRepository.findByName(req.target)

//        val tmpInt: TemplateInt = TemplateInt(1)
//        val restTemplate: RestTemplate = RestTemplate()
//        var res: String = restTemplate.postForObject(raspConfig.baseUrl, tmpInt, String::class.java)

        val log = Log(sender.name, sender.location, target!!.name, target.location, req.subject, req.note, "success", Date())
        logRepository.save(log)

        return "Success"
    }
    
    @RequestMapping(value = "/history", method = arrayOf(RequestMethod.GET))
    fun showHistory(): ArrayList<LogForm>{
        val user = SecurityContextHolder.getContext().authentication.principal as User
        print("++++++++++++++++ Username :: "+user.username+" ++++++++++++++++ ")
        val log: ArrayList<Log> = logRepository.findBySenderOrderByCreatedAt(user.name)!!
        val logList: ArrayList<LogForm> = ArrayList()
        log.mapTo(logList) { LogForm(it.sender, it.target, it.subject, it.note, it.createdAt, it.status) }
        return logList
    }
}