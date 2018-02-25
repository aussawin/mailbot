package com.egco.mailbot.controller

import com.egco.mailbot.config.FirebaseConfig
import com.egco.mailbot.config.RaspConfig
import com.egco.mailbot.dao.CallingReqire
import com.egco.mailbot.dao.Location
import com.egco.mailbot.dao.LogTemplate
import com.egco.mailbot.dao.Post
import com.egco.mailbot.domain.Log
import com.egco.mailbot.domain.User
import com.egco.mailbot.exception.ResourceNotFoundException
import com.egco.mailbot.repository.BotPositionRepository
import com.egco.mailbot.repository.LogRepository
import com.egco.mailbot.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.util.*
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import kotlin.collections.ArrayList
import com.google.firebase.auth.FirebaseCredentials
import java.io.FileInputStream


@RestController
@RequestMapping(value = "/api/controller")
@CrossOrigin("*")
class AppController(val userRepository: UserRepository,
                    val logRepository: LogRepository,
                    val botPositionRepository: BotPositionRepository,
                    val raspConfig: RaspConfig) {
    private val firebaseConfig = FirebaseConfig()

    @RequestMapping(value = "/call", method = arrayOf(RequestMethod.POST))
    fun calling(@RequestBody req: CallingReqire): String {
        val sender = SecurityContextHolder.getContext().authentication.principal as User

        val target = if (userRepository.existsByName(req.target)) {
            userRepository.findByName(req.target)
        } else {
            throw ResourceNotFoundException("Cannot found target name : ${req.target} !")
        }
        var current = botPositionRepository.findByState("current")

        val statusList = arrayListOf("calling", "waitForSender", "sending", "waitForTarget",
                "verifySender", "verifyTarget", "waitForPacking", "waitForPickUp", "returning")
        var isQueue = false
        statusList
                .filter { logRepository.findByStatus(it) != null }
                .forEach { isQueue = true }

        var location: Location
        current.status = if (isQueue) {
            // if there are Queue -> wait
            "wait"
        } else {
            var currentLocation = botPositionRepository.findByState("current").position
            location = Location(currentLocation, sender.location)
            // if no Queue -> trigger robot API
            val restTemplate = RestTemplate()
            val res: String = restTemplate.postForObject(raspConfig.CALL, location, String::class.java)
            // and send notification to client
            if (res == "okay") {
                FirebaseController().send(req.target, "Robot is now going to ${sender.name}", firebaseConfig.MESSAGE_PATH)
            }
            "calling"
        }

        print("\n$sender is calling to send to $target")

        val log = Log(sender.name, sender.location, target!!.name, target.location, req.subject, req.note, current.status, Date())
        logRepository.save(log)
        botPositionRepository.save(current)
        return "Success"
    }

    @RequestMapping(value = "/history", method = arrayOf(RequestMethod.GET))
    fun showHistory(): ArrayList<LogTemplate> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val log: ArrayList<Log> = logRepository.findBySenderOrderByCreatedAt(user.name)!!
        val logList: ArrayList<LogTemplate> = ArrayList()
        log.mapTo(logList) { LogTemplate(it.sender, it.target, it.subject, it.note, it.createdAt, it.status) }
        return logList
    }

    @RequestMapping(value = "/queue", method = arrayOf(RequestMethod.GET))
    fun showQueue(): ArrayList<LogTemplate> {
        val log: ArrayList<Log> = logRepository.findByStatusOrderByCreatedAt("wait")!!
        val logList: ArrayList<LogTemplate> = ArrayList()
        log.mapTo(logList) { LogTemplate(it.sender, it.target, it.subject, it.note, it.createdAt, it.status) }
        return logList
    }

    @RequestMapping(value = "/countQueue", method = arrayOf(RequestMethod.GET))
    fun countQueue(): Int {
        val log: ArrayList<Log> = logRepository.findByStatusOrderByCreatedAt("wait")!!
        return log.size
    }

    @RequestMapping(value = "/getTargetName", method = arrayOf(RequestMethod.GET))
    fun getTargetName(): ArrayList<String> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val target = userRepository.findByUsernameIsNotNull()
        val targetName: ArrayList<String> = arrayListOf()
        target!!
                .filter { it.name != user.name }
                .mapTo(targetName) { it.name }
        return targetName
    }

    @RequestMapping(value = "/getName", method = arrayOf(RequestMethod.GET))
    fun getName(): String {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        return user.name
    }

}