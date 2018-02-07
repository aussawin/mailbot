package com.egco.mailbot.controller

import com.egco.mailbot.config.RaspConfig
import com.egco.mailbot.dao.FaceRequest
import com.egco.mailbot.dao.Post
import com.egco.mailbot.dao.SetCurrentPosition
import com.egco.mailbot.domain.Log
import com.egco.mailbot.exception.ResourceNotFoundException
import com.egco.mailbot.exception.StatusNotFoundException
import com.egco.mailbot.repository.BotPositionRepository
import com.egco.mailbot.repository.LogRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = "/api/botController")
@CrossOrigin("*")
class BotController(val logRepository: LogRepository,
                    val botPositionRepository: BotPositionRepository,
                    val raspConfig: RaspConfig) {

    private var count: Int = 0
    private var currentState = "calling"

    @RequestMapping(value = "/setPosition", method = arrayOf(RequestMethod.PATCH))
    fun setPosition(@RequestBody setCurrentPosition: SetCurrentPosition){
        val current = botPositionRepository.findByState("current")
        val previous = botPositionRepository.findByState("previous")
        previous.position = current.position
        current.position = setCurrentPosition.position
        botPositionRepository.save(current)
        botPositionRepository.save(previous)
    }

    @RequestMapping(value = "/changeStatus", method = arrayOf(RequestMethod.PATCH))
    fun changeStatus() {
        //Calling + Sending + Returning
        if (logRepository.findByStatus(currentState) != null){
            var log = logRepository.findByStatus(currentState)!!
            val target = log.target
            val sender = log.sender
            val post: Post

            currentState = when (log.status){
                "calling" -> {
                    post = Post(sender, "Robot is ready to receive message from you!")
                    FirebaseController().send(post)
                    "waitForSender"
                }
                "sending" -> {
                    post = Post(sender, "The message was send to $target correctly.")
                    FirebaseController().send(post)
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
            log.status = currentState
            logRepository.save(log)
            if (log.status == "done" || log.status == "failed"){
                val waitList = logRepository.findByStatusOrderByCreatedAt("wait")
                if (waitList!!.isNotEmpty()){
                    val nextQueue = waitList[0]
                    nextQueue.status = "calling"
                    val post = Post(nextQueue.sender, "Robot is going to pick up from you.")
                    FirebaseController().send(post)
                    logRepository.save(nextQueue)
                }
            }
        }
        else{
            throw ResourceNotFoundException()
        }
    }

    @RequestMapping(value = "/pressing", method = arrayOf(RequestMethod.PATCH))
    fun pressMethods(){
        //WaitForSender + WaitForTarget
        if (logRepository.findByStatus(currentState) != null){
            var log = logRepository.findByStatus(currentState)!!
            currentState = when (log.status){
                "waitForSender" -> {
                    //@todo Trigger to unlock
                    "sending"
                }
                "waitForTarget" -> {
                    //@todo Trigger to open the camera
                    "verifying"
                }
                else -> throw StatusNotFoundException(status = log.status)
            }
            log.status = currentState
            logRepository.save(log)
        }
        else throw ResourceNotFoundException()
    }

    @RequestMapping(value = "/sendFaceName", method = arrayOf(RequestMethod.PATCH))
    fun isUser(@RequestBody req: FaceRequest) {
        // Verifying
        if (logRepository.findByStatus(currentState) != null) {
            var log = logRepository.findByStatus(currentState)!!
            val target = log.target
            val sender = log.sender
            if (currentState == "verifying"){
                if (req.name == log.target){
                    //@todo UNLOCK
                    count = 0
                } else {
                    //@todo trigger camera again
                    count ++
                    if (count == 3){
                        //@todo returning
                    }
                }
            }
            else throw ResourceNotFoundException()
        } else throw ResourceNotFoundException()
    }
}
