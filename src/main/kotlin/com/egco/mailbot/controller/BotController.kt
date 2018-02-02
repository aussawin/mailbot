package com.egco.mailbot.controller

import com.egco.mailbot.config.FirebaseConfig
import com.egco.mailbot.config.RaspConfig
import com.egco.mailbot.dao.FaceRequest
import com.egco.mailbot.dao.Post
import com.egco.mailbot.domain.Log
import com.egco.mailbot.repository.LogRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = "/api/botController")
@CrossOrigin("*")
class BotController(val logRepository: LogRepository,
                    val raspConfig: RaspConfig) {


    private var count: Int = 0

    @RequestMapping(value = "/changeStatus", method = arrayOf(RequestMethod.PATCH))
    fun changeStatus() {
        var log: Log? = null
        arrayListOf("calling", "sending", "verifying", "returning")
                .filter { logRepository.findByStatus(it) != null }
                .forEach { log = logRepository.findByStatus(it) }
        if (log != null){
            when (log!!.status){
                "calling" -> {
                    log!!.status = "sending"
                    val post = Post(log!!.target, "Robot is sending ${log!!.subject} from ${log!!.sender} to you!")
                    FirebaseController().send(post)
                }
                "sending" -> log!!.status = "verifying"
                "verifying" -> log!!.status = "done"
                "returning" -> log!!.status = "failed"
            }
            logRepository.save(log)
            if (log!!.status == "done" || log!!.status == "failed"){
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
    }

    @RequestMapping(value = "/sendFaceName", method = arrayOf(RequestMethod.PATCH))
    fun isUser(@RequestBody req: FaceRequest) {
        if (logRepository.existsByTargetAndStatus(req.name, "verifying")) {
            //@todo Trigger to UNLOCK
            count = 0
        } else {
            //@todo Trigger to Detect again
            count += 1
            if (count == 3) {
                //@todo Reject Request
                val log = logRepository.findByStatus("verifying")
                log!!.status = "returning"
                logRepository.save(log)
            }
        }
    }
}
