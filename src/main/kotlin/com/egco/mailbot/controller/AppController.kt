package com.egco.mailbot.controller

import com.egco.mailbot.config.RaspConfig
import com.egco.mailbot.dao.CallingReqire
import com.egco.mailbot.dao.Location
import com.egco.mailbot.dao.LogTemplate
import com.egco.mailbot.domain.Log
import com.egco.mailbot.domain.User
import com.egco.mailbot.exception.ResourceNotFoundException
import com.egco.mailbot.repository.LogRepository
import com.egco.mailbot.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.util.*
import com.egco.mailbot.service.AndroidPushNotificationsService
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import java.util.concurrent.CompletableFuture
import org.springframework.http.HttpEntity
import java.util.concurrent.ExecutionException
import kotlin.collections.ArrayList


@RestController
@RequestMapping(value = "/api/controller")
@CrossOrigin("*")
class AppController(val userRepository: UserRepository,
                    val logRepository: LogRepository,
                    val raspConfig: RaspConfig) {

    private val TOPIC = "JavaSampleApproach"
    var androidPushNotificationsService: AndroidPushNotificationsService? = null

    @RequestMapping(value = "/call", method = arrayOf(RequestMethod.POST))
    fun calling(@RequestBody req: CallingReqire): String{
        val sender = SecurityContextHolder.getContext().authentication.principal as User

        val target = if (userRepository.existsByName(req.target)) { userRepository.findByName(req.target) }
        else { throw ResourceNotFoundException("Cannot found target name : ${req.target} !") }

        val statusList = arrayListOf("calling", "sending", "verifying")
        var isQueue = false

        statusList
                .filter { logRepository.findByStatus(it) != null }
                .forEach { isQueue = true }

        val status = if (isQueue){ "wait" }
        else{ "calling" }


        val location = Location(sender.location.toString())
        val restTemplate = RestTemplate()
        val res: String = restTemplate.postForObject(raspConfig.baseUrl, location, String::class.java)
        print("\n>>>>>>>>>>>>>>>> Response from rasp : $res <<<<<<<<<<<<<<<<<<<<<\n")

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

    @RequestMapping(value = "/countQueue", method = arrayOf(RequestMethod.GET))
    fun countQueue(): Int{
        val log: ArrayList<Log> = logRepository.findByStatusOrderByCreatedAt("wait")!!
        return log.size
    }

    @RequestMapping(value = "/getTargetName", method = arrayOf(RequestMethod.GET))
    fun getTargetName(): ArrayList<String>{
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val target = userRepository.findByUsernameIsNotNull()
        val targetName: ArrayList<String> = arrayListOf()
        target!!
                .filter { it.name!=user.name }
                .mapTo(targetName) { it.name }
        return targetName
    }

    @RequestMapping(value = "/send", method = arrayOf(RequestMethod.GET))
    fun send(): ResponseEntity<String> {
        val body = JSONObject()
        body.put("to", "/topics/" + TOPIC)
        body.put("priority", "high")

        val notification = JSONObject()
        notification.put("title", "JSA Notification")
        notification.put("body", "Happy Message!")

        val data = JSONObject()
        data.put("Key-1", "JSA Data 1")
        data.put("Key-2", "JSA Data 2")

        body.put("notification", notification)
        body.put("data", data)

        val request = HttpEntity(body.toString())

        val pushNotification = androidPushNotificationsService!!.send(request)
        CompletableFuture.allOf(pushNotification).join()

        try {
            val firebaseResponse = pushNotification.get()

            return ResponseEntity(firebaseResponse, HttpStatus.OK)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }


        return ResponseEntity("Push Notification ERROR!", HttpStatus.BAD_REQUEST)
    }

}