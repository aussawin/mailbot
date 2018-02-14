package com.egco.mailbot.controller

import com.egco.mailbot.config.RaspConfig
import com.egco.mailbot.dao.FaceRequest
import com.egco.mailbot.dao.Post
import com.egco.mailbot.dao.SetCurrentPosition
import com.egco.mailbot.domain.Log
import com.egco.mailbot.exception.RaspStatusError
import com.egco.mailbot.exception.StatusNotFoundException
import com.egco.mailbot.repository.BotPositionRepository
import com.egco.mailbot.repository.LogRepository
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping(value = "/api/botController")
@CrossOrigin("*")
class BotController(val logRepository: LogRepository,
                    val botPositionRepository: BotPositionRepository,
                    val raspConfig: RaspConfig) {

    private var count: Int = 0
    private var currentState = "calling"
    private val restTemplate = RestTemplate()

    @RequestMapping(value = "/setPosition", method = arrayOf(RequestMethod.PATCH))
    fun setPosition(@RequestBody setCurrentPosition: SetCurrentPosition){
        val current = botPositionRepository.findByState("current")
        current.position = setCurrentPosition.message
        botPositionRepository.save(current)
    }

    @RequestMapping(value = "/changeStatus", method = arrayOf(RequestMethod.PATCH))
    fun changeStatus() {
        //Calling + Sending + Returning
        var current = botPositionRepository.findByState("current")
        if (logRepository.findByStatus(current.status) != null){
            var log = logRepository.findByStatus(currentState)!!
            val target = log.target
            val sender = log.sender

            current.status = when (log.status){
                "calling" -> {
                    FirebaseController().send(sender, "Robot is ready to receive message from you!")
                    "waitForSender"
                }
                "sending" -> {
                    FirebaseController().send(target, "Robot is ready to sending message to you!")
                    "waitForTarget"
                }
                "returning" -> {
                    //@todo Handle for returning to sender
                    "failed"
                }
                else -> {
                    throw StatusNotFoundException(status = log.status)
                }
            }
            log.status = current.status
            logRepository.save(log)
            botPositionRepository.save(current)
        }
        else{
            throw StatusNotFoundException(status = currentState)
        }
    }

    @RequestMapping(value = "/pressing", method = arrayOf(RequestMethod.PATCH))
    fun pressMethods(){
        //WaitForSender + WaitForTarget
        var current = botPositionRepository.findByState("current")
        if (logRepository.findByStatus(current.status) != null){
            var log = logRepository.findByStatus(current.status)!!
            current.status = when (log.status){
                "waitForSender" -> {
                    // UNLOCK //
                    val res: String = restTemplate.getForObject(raspConfig.UNLOCK, String::class.java)
                    resIsOK(res)
                    "sending"
                }
                "waitForTarget" -> {
                    // OPEN CAMERA //
                    val res: String = restTemplate.getForObject(raspConfig.OPENCAM, String::class.java)
                    resIsOK(res)
                    "verifying"
                }
                else -> throw StatusNotFoundException(status = log.status)
            }
            log.status = current.status
            botPositionRepository.save(current)
            logRepository.save(log)
        }
        else throw StatusNotFoundException(status = currentState)
    }

    @RequestMapping(value = "/sendFaceName", method = arrayOf(RequestMethod.PATCH))
    fun isUser(@RequestBody req: FaceRequest) {
        // Verifying
        var current = botPositionRepository.findByState("current")
        if (logRepository.findByStatus(current.status) != null) {
            var log = logRepository.findByStatus(current.status)!!
            val target = log.target
            val sender = log.sender
            if (current.status == "verifying"){
                if (req.message == log.target){
                    // CALLING //
                    val res: String = restTemplate.getForObject(raspConfig.CALL, String::class.java)
                    resIsOK(res)
                    count = 0
                    log.status = "done"
                    logRepository.save(log)
                    FirebaseController().send(sender, "The message is sending to $target correctly.")
                    val waitList = logRepository.findByStatusOrderByCreatedAt("wait")
                    if (waitList!!.isNotEmpty()){
                        val nextQueue = waitList[0]
                        nextQueue.status = "calling"
                        FirebaseController().send(nextQueue.sender, "Robot is going to pick up from you.")
                        logRepository.save(nextQueue)
                        current.status = "calling"
                    } else {
                        current.status = "wait"
                    }
                    botPositionRepository.save(current)
                } else {
                    // OPEN CAMERA //
                    val res: String = restTemplate.getForObject(raspConfig.OPENCAM, String::class.java)
                    resIsOK(res)
                    count ++
                    if (count == 3){
                        current.status = "returning"
                        //@todo returning
                    }
                }
            }
            else throw StatusNotFoundException(status = current.status)
        } else throw StatusNotFoundException(status = current.status)
    }

    fun resIsOK(res: String){
        if (res != "okay") throw RaspStatusError(status = res)
    }
}