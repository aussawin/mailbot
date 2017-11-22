package com.egco.mailbot.controller

import com.egco.mailbot.config.RaspConfig
import com.egco.mailbot.dao.CallingReqire
import com.egco.mailbot.dao.FaceRequest
import com.egco.mailbot.dao.LogTemplate
import com.egco.mailbot.domain.Log
import com.egco.mailbot.domain.User
import com.egco.mailbot.exception.ResourceNotFoundException
import com.egco.mailbot.repository.LogRepository
import com.egco.mailbot.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

@RestController
@RequestMapping(value = "/api/botController")
@CrossOrigin("*")
class BotController(val logRepository: LogRepository,
                    val raspConfig: RaspConfig) {

    private var count: Int = 0

    @RequestMapping(value = "/changeStatus", method = arrayOf(RequestMethod.PATCH))
    fun changeStatus() {
        val statusList = arrayListOf("calling", "sending", "verifying")
        for (i in 0 until statusList.count()-1){
            val callLog = logRepository.findByStatus(statusList[i])
            if (callLog != null){
                callLog.status = if (i==statusList.count()-1){ "done" }
                else { statusList[i+1] }
            }
            logRepository.save(callLog)
        }
        val waitList = logRepository.findByStatusOrderByCreatedAt("wait")
        if (waitList!!.isNotEmpty()){
            waitList[0].status = "calling"
            logRepository.save(waitList)
        }
    }

    @RequestMapping(value = "/sendFaceName", method = arrayOf(RequestMethod.PATCH))
    fun isUser(@RequestBody req: FaceRequest) {
        if (logRepository.existsByTargetAndStatus(req.name, "verifying")) {
            //@todo Trigger to UNLOCK
            count = 0
        }
        else{
            //@todo Trigger to Detect again
            count += 1
            if (count == 3){
                //@todo Reject Request
            }
        }

    }
}